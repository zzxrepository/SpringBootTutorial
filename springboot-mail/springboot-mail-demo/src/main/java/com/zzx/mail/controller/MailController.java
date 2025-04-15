package com.zzx.mail.controller;

import com.zzx.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    /**
     * 测试发送简单文本邮件
     * curl "http://localhost:8080/mail/simple?to=1849975198@qq.com"
     */
    @GetMapping("/simple")
    public ResponseEntity<String> sendSimpleMail(@RequestParam String to) {
        mailService.sendSimpleMail(to, "测试简单邮件", "这是一封简单文本邮件。");
        return ResponseEntity.ok("简单邮件已发送");
    }

    /**
     * 测试发送 HTML 邮件（使用 FreeMarker 模板）
     * curl "http://localhost:8080/mail/html?to=1849975198@qq.com&userName=毛毛张"
     */
    @GetMapping("/html")
    public ResponseEntity<String> sendHtmlMail(@RequestParam String to,
                                               @RequestParam String userName) {
        // 渲染模板
        Map<String, Object> model = new HashMap<>();
        model.put("userName", userName);
        String htmlContent = mailService.renderTemplate("demo.ftl", model);

        // 发送邮件
        mailService.sendHtmlMail(to, "测试 HTML 邮件", htmlContent);
        return ResponseEntity.ok("HTML 邮件已发送");
    }

    /**
     * 测试发送带附件的 HTML 邮件
     * curl -X POST -F "files=@d:/Downloads/Hot100.pdf" "http://localhost:8080/api/mail/html-with-attachment?to=1849975198@qq.com&userName=mmzhang"
     */
    @PostMapping(value = "/html-with-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendHtmlMailWithAttachment(@RequestParam String to,
                                                             @RequestParam String userName,
                                                             @RequestParam("files") List<MultipartFile> files) {
        try {
            // 渲染模板
            Map<String, Object> model = new HashMap<>();
            model.put("userName", userName);
            String htmlContent = mailService.renderTemplate("demo.ftl", model);

            // 将 MultipartFile 转换为临时文件
            List<File> attachments = files.stream()
                    .map(file -> {
                        try {
                            File tempFile = File.createTempFile("attachment-", "-" + file.getOriginalFilename());
                            file.transferTo(tempFile);
                            return tempFile;
                        } catch (IOException e) {
                            throw new RuntimeException("文件处理失败", e);
                        }
                    })
                    .collect(Collectors.toList());

            // 发送邮件
            mailService.sendHtmlMailWithAttachment(to, "测试带附件的 HTML 邮件", htmlContent, attachments);

            // 清理临时文件
            attachments.forEach(File::delete);

            return ResponseEntity.ok("带附件的 HTML 邮件已发送");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("发送失败: " + e.getMessage());
        }
    }
}