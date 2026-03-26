package kr.kh.kihibooks.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.utils.SecurityService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FileController {

    @Value("${spring.path.upload}")
    private String uploadPath;

    @Autowired
    private SecurityService securityService;

    /**
     * 비정형 데이터(EPUB, 이미지)에 대한 보안 접근 제어
     * 1. EPUB 파일: 구매자 또는 출판사 관계자만 접근 가능
     * 2. 이미지 파일: 공개 데이터로 취급 (필요 시 권한 추가 가능)
     */
    @GetMapping("/file/{boCode}/{type}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @PathVariable("boCode") String boCode,
            @PathVariable("type") String type,
            @PathVariable("fileName") String fileName,
            HttpServletRequest request) {

        // 1. 보안 체크: EPUB 파일인 경우 엄격한 RBAC 적용
        if ("epubs".equals(type)) {
            // 파일명에서 회차 코드 추출 (ep_code.epub -> ep_code)
            String epCode = fileName.substring(0, fileName.lastIndexOf("."));
            if (!securityService.canAccessEpisode(epCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // 2. 파일 경로 생성 및 존재 여부 확인
        File file = new File(uploadPath + File.separator + boCode + File.separator + type + File.separator + fileName);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 3. 리소스 생성 및 Content-Type 설정
        Resource resource = new FileSystemResource(file);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // 타입 추론 실패 시 기본값 설정
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
