package com.zzx.mail.service;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface MailService {

    /**
     * 发送简单文本邮件
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param content 文本内容
     */
    void sendSimpleMail(String toEmail, String subject, String content);

    /**
     * 发送 HTML 内容邮件
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML 内容
     */
    void sendHtmlMail(String toEmail, String subject, String htmlContent);

    /**
     * 发送带附件的 HTML 邮件
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML 内容
     * @param attachments 附件列表（文件路径）
     */
    void sendHtmlMailWithAttachment(String toEmail, String subject, String htmlContent, List<File> attachments);

    /**
     * 渲染 FreeMarker 模板
     * @param templateName 模板文件名（如 "demo.ftl"）
     * @param model 模板参数
     * @return 渲染后的 HTML 内容
     */
    String renderTemplate(String templateName, Map<String, Object> model);
}