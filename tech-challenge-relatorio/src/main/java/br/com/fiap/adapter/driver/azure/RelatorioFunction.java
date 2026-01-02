package br.com.fiap.adapter.driver.azure;

import br.com.fiap.core.usecase.GerarRelatorioUseCase;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import jakarta.inject.Inject;

public class RelatorioFunction {

    @Inject
    GerarRelatorioUseCase useCase;

    @FunctionName("RelatorioSemanal")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 */2 * * * *") String timerInfo,
            ExecutionContext context
    ) {
        context.getLogger().info("⏰ Iniciando Relatório Semanal Serverless...");

        try {
            useCase.executar();
            context.getLogger().info("✅ Relatório gerado e enviado com sucesso.");
        } catch (Exception e) {
            context.getLogger().severe("❌ Erro ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
        }
    }
}