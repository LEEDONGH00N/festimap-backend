package com.halo.eventer.domain.festival.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.halo.eventer.domain.festival.Festival;
import com.halo.eventer.domain.festival.dto.*;
import com.halo.eventer.domain.festival.exception.FestivalAlreadyExistsException;
import com.halo.eventer.domain.festival.exception.FestivalNotFoundException;
import com.halo.eventer.domain.festival.repository.FestivalRepository;
import com.halo.eventer.domain.image.dto.FileDto;
import com.halo.eventer.domain.member.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
public class FestivalServiceTest {

    @Mock
    private FestivalRepository festivalRepository;

    @InjectMocks
    private FestivalService festivalService;

    private Festival festival;
    private FestivalCreateDto festivalCreateDto;
    private Member owner;

    @BeforeEach
    void setUp() {
        festivalCreateDto = new FestivalCreateDto("축제", "univ");
        festival = Festival.from(festivalCreateDto);
        owner = Member.createAgency("agency", "password", "test@test.com", "테스트회사", "홍길동", "대리", "01012345678");
    }

    @Test
    void 축제생성_성공() {
        // given
        given(festivalRepository.findByName(anyString())).willReturn(Optional.empty());
        given(festivalRepository.findBySubAddress(anyString())).willReturn(Optional.empty());
        given(festivalRepository.save(any())).willReturn(festival);

        // when
        festivalService.create(festivalCreateDto, owner);

        // then
        verify(festivalRepository, times(1))
                .save(argThat(savedFestival -> savedFestival.getName().equals(festivalCreateDto.getName())
                        && savedFestival.getSubAddress().equals(festivalCreateDto.getSubDomain())));
    }

    @Test()
    void 축제생성_이름이같은_경우() {
        // given
        given(festivalRepository.findByName(anyString())).willReturn(Optional.of(festival));

        // when & then
        assertThatThrownBy(() -> festivalService.create(festivalCreateDto, owner))
                .isInstanceOf(FestivalAlreadyExistsException.class);
    }

    @Test
    void 축제생성_서브주소같은_경우() {
        // given
        given(festivalRepository.findBySubAddress(anyString())).willReturn(Optional.of(festival));

        // when & then
        assertThatThrownBy(() -> festivalService.create(festivalCreateDto, owner))
                .isInstanceOf(FestivalAlreadyExistsException.class);
    }

    @Test
    void 축제조회_성공() {
        // given
        given(festivalRepository.findById(1L)).willReturn(Optional.of(festival));

        // when
        FestivalResDto result = festivalService.findById(1L);

        // then
        assertThat(result.getName()).isEqualTo(festival.getName());
    }

    @Test
    void 축제조회_축제없는_경우() {
        // given
        given(festivalRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> festivalService.findById(1L)).isInstanceOf(FestivalNotFoundException.class);
    }

    @Test
    void 축제목록조회_성공() {
        // given
        given(festivalRepository.findAll()).willReturn(List.of(festival));

        // when
        List<FestivalSummaryDto> result = festivalService.findAll();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getFestivalName()).isEqualTo("축제");
        assertThat(result.get(0).getSubAddress()).isEqualTo("univ");
    }

    @Test
    void 축제목록조회_결과없음() {
        // given
        given(festivalRepository.findAll()).willReturn(Collections.emptyList());

        // when
        List<FestivalSummaryDto> result = festivalService.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 축제수정_성공() {
        // given
        FestivalCreateDto updateDto = new FestivalCreateDto("업데이트된 축제", "test-festival");
        given(festivalRepository.findById(1L)).willReturn(Optional.of(festival));

        // when
        FestivalResDto result = festivalService.update(1L, updateDto);

        // then
        assertThat(result.getName()).isEqualTo("업데이트된 축제");
    }

    @Test
    void 축제삭제_성공() {
        // given
        given(festivalRepository.findById(1L)).willReturn(Optional.of(festival));

        // when
        festivalService.delete(1L);

        // then
        verify(festivalRepository, times(1)).delete(festival);
    }

    @Test
    void 축제삭제_실패_DataIntegrityViolationException() {
        // given
        Long festivalId = 1L;
        given(festivalRepository.findById(festivalId)).willReturn(Optional.of(festival));
        willThrow(new DataIntegrityViolationException("삭제 실패"))
                .given(festivalRepository)
                .delete(festival);

        // when & then
        assertThatThrownBy(() -> festivalService.delete(festivalId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("삭제 실패");
    }

    @Test
    void 축제색등록_성공() {
        // given
        ColorDto colorDto = new ColorDto();
        given(festivalRepository.findById(1L)).willReturn(Optional.of(festival));

        // when
        festivalService.updateColor(1L, colorDto);

        // then
        assertThat(festival.getBackgroundColor()).isEqualTo(colorDto.getBackgroundColor());
        assertThat(festival.getSubColor()).isEqualTo(colorDto.getSubColor());
        assertThat(festival.getMainColor()).isEqualTo(colorDto.getMainColor());
        assertThat(festival.getFontColor()).isEqualTo(colorDto.getFontColor());
    }

    @Test
    void 로고등록_성공() {
        // given
        given(festivalRepository.findById(1L)).willReturn(Optional.of(festival));
        FileDto fileDto = new FileDto();

        // when
        festivalService.updateLogo(1L, fileDto);

        // then
        assertThat(festival.getLogo()).isEqualTo(fileDto.getUrl());
    }

    @Test
    void subAddress조회_성공() {
        // given
        given(festivalRepository.findBySubAddress("univ")).willReturn(Optional.of(festival));

        // when
        FestivalSummaryDto festivalSummaryDto = festivalService.findBySubAddress("univ");

        // then
        assertThat(festivalSummaryDto).isNotNull();
        assertThat(festivalSummaryDto.getFestivalName()).isEqualTo("축제");
        assertThat(festivalSummaryDto.getSubAddress()).isEqualTo("univ");
    }

    @Test
    void subAddress조회_축제가없는_경우() {
        // given
        given(festivalRepository.findBySubAddress("univ")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> festivalService.findBySubAddress("univ"))
                .isInstanceOf(FestivalNotFoundException.class);
    }

    @Test
    void 축제위치등록_성공() {
        // given
        given(festivalRepository.findById(1L)).willReturn(Optional.of(festival));
        FestivalLocationDto festivalLocationDto = new FestivalLocationDto();

        // when
        FestivalResDto result = festivalService.updateLocation(1L, festivalLocationDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLongitude()).isEqualTo(festival.getLongitude());
        assertThat(result.getLatitude()).isEqualTo(festival.getLatitude());
    }
}
