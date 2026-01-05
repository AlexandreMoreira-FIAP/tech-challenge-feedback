package br.com.fiap.adapter.driver.azure;

import br.com.fiap.core.usecase.GerarRelatorioUseCase;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RelatorioWorker {

    @Inject
    Logger LOG;

    @Inject
    GerarRelatorioUseCase useCase;

    @Scheduled(every = "150s")
    public void run() {
        LOG.info("Iniciando processamento do Relatório Semanal via Worker...");
        try {
            useCase.executar();
            LOG.info("Relatório processado com sucesso.");
        } catch (Exception e) {
            LOG.error("Erro ao processar relatório", e);
        }
    }
}