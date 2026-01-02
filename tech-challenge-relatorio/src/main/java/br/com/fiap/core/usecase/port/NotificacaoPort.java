package br.com.fiap.core.usecase.port;

public interface NotificacaoPort {
    void enviarRelatorio(String destinatario, String assunto, String mensagemHtml);
}