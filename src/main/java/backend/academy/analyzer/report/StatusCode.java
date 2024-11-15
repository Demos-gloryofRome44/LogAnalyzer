package backend.academy.analyzer.report;

import org.apache.http.impl.EnglishReasonPhraseCatalog;

public class StatusCode {
    /**
     * Получает текстовое описание кода статуса.
     *
     * @param code Код статуса в виде строки.
     * @return Описание статуса или "Unknown status", если код не распознан.
     */
    public String getDescription(String code) {
        try {
            int statusCode = Integer.parseInt(code);
            return EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, null);
        } catch (NumberFormatException e) {
            return "Unknown status";
        }
    }
}
