package com.halo.eventer.domain.image.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.halo.eventer.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "이미지", description = "이미지 업로드 관련 API")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 업로드 URL 발급", description = "AWS S3에 이미지를 업로드하기 위한 Pre-Signed URL을 발급받습니다.")
    @GetMapping("/upload/preSigned")
    public String getUploadUrl(@RequestParam String fileExtension) {
        return imageService.generatePreSignedUploadUrl(fileExtension);
    }
}
