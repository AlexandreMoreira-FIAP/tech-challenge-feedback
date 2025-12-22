package br.com.fiap.core.port;

public interface NotificadorEmailPort {
    void enviarEmail(String destinatario, String assunto, String corpo);
}