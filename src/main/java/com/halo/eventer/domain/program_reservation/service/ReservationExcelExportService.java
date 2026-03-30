package com.halo.eventer.domain.program_reservation.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import com.halo.eventer.domain.program_reservation.repository.ReservationExcelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationExcelExportService {
    private final ReservationExcelRepository reservationExcelRepository;

    private static final List<String> RESERVATION_HEADERS =
            List.of("신청자", "신청자 전화번호", "방문자", "방문자 전화번호", "프로그램명", "신청날짜", "신청회차", "소요시간", "참여인원", "결제금액", "예약상태");

    private static final int[] COLUMN_WIDTHS = {14, 20, 14, 20, 18, 16, 10, 10, 10, 14, 14};

    private static final List<ProgramReservationStatus> EXPORT_STATUSES =
            List.of(ProgramReservationStatus.CONFIRMED, ProgramReservationStatus.CANCELED);

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> export(Condition condition) {
        List<String> names =
                reservationExcelRepository.findFestivalNameById(condition.festivalId, PageRequest.of(0, 1));
        String festivalName = names.isEmpty() ? "행사" : names.get(0);
        String filename = buildFilename(condition, festivalName);
        String encodedFilename =
                URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        try (SXSSFWorkbook wb = new SXSSFWorkbook(500);
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            wb.setCompressTempFiles(true);
            Styles styles = createStyles(wb);

            SXSSFSheet sheet = wb.createSheet("예약정보");
            sheet.trackAllColumnsForAutoSizing();

            setColumnWidths(sheet, COLUMN_WIDTHS);
            createHeaderRow(sheet, RESERVATION_HEADERS, styles);

            AtomicInteger rowNum = new AtomicInteger(1);
            try (Stream<ReservationExcelRepository.ReservationExcelRowView> stream =
                    reservationExcelRepository.streamByFestivalAndSlotDateBetween(
                            condition.festivalId, condition.from, condition.to, EXPORT_STATUSES)) {
                stream.forEach(view -> {
                    Row row = sheet.createRow(rowNum.getAndIncrement());
                    List<Object> cells = toCells(view);
                    for (int col = 0; col < cells.size(); col++) {
                        Cell cell = row.createCell(col);
                        setCellValue(cell, cells.get(col), styles);
                    }
                });
            }

            wb.write(out);
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);

        } catch (IOException e) {
            log.error("Excel export failed", e);
            throw new RuntimeException("Excel export failed", e);
        }
    }

    private List<Object> toCells(ReservationExcelRepository.ReservationExcelRowView v) {
        LocalTime startAt = v.getStartTime();

        String time = "";
        if (startAt != null) {
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
            time = startAt.format(tf);
        }

        String peopleCount = "";
        if (v.getPeopleCount() != null) {
            peopleCount = v.getPeopleCount().toString();
        }

        String price = "";
        if (v.getPrice() != null && v.getPeopleCount() != null) {
            price = String.valueOf(v.getPrice() * v.getPeopleCount());
        }

        ProgramReservationStatus status = v.getStatus();
        String statusLabel = status == null ? "" : status.getLabel();

        return Arrays.asList(
                nvl(v.getBookerName()),
                nvl(v.getBookerPhone()),
                nvl(v.getVisitorName()),
                nvl(v.getVisitorPhone()),
                nvl(v.getProgramName()),
                v.getReservationDate(),
                time,
                nvl(v.getDurationTime()),
                peopleCount,
                price,
                statusLabel);
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

    private String buildFilename(Condition c, String festivalName) {
        if (c.from != null && c.to != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fromStr = c.from.format(formatter);
            String toStr = c.to.format(formatter);
            return "[" + festivalName + "]예약리스트_" + fromStr + "_to_" + toStr + ".xlsx";
        }
        return "프로그램_예약_정보.xlsx";
    }

    private Styles createStyles(Workbook wb) {
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle bodyStyle = wb.createCellStyle();
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.cloneStyleFrom(bodyStyle);
        DataFormat dataFormat = wb.createDataFormat();
        dateStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd"));

        return new Styles(headerStyle, bodyStyle, dateStyle);
    }

    private void setColumnWidths(Sheet sheet, int[] widths) {
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i] * 256);
        }
    }

    private void createHeaderRow(Sheet sheet, List<String> headers, Styles styles) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(styles.headerStyle);
        }
    }

    private void setCellValue(Cell cell, Object value, Styles styles) {
        if (value == null) {
            cell.setCellValue("");
            cell.setCellStyle(styles.bodyStyle);
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
            cell.setCellStyle(styles.dateStyle);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
            cell.setCellStyle(styles.bodyStyle);
        } else {
            cell.setCellValue(value.toString());
            cell.setCellStyle(styles.bodyStyle);
        }
    }

    // ===== Inner types =====

    public static class Condition {
        private final Long festivalId;
        private final LocalDate from;
        private final LocalDate to;

        public Condition(Long festivalId, LocalDate from, LocalDate to) {
            this.festivalId = festivalId;
            this.from = from;
            this.to = to;
        }
    }

    private record Styles(CellStyle headerStyle, CellStyle bodyStyle, CellStyle dateStyle) {}
}
