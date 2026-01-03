package br.com.fiap.adapter.driver.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

public class RelatorioFunction {

    @FunctionName("RelatorioSemanal")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 */5 * * * *") String timerInfo,
            final ExecutionContext context
    ) {
        // Log simples direto no contexto da Azure
        context.getLogger().info("🔥🔥🔥 TESTE DE VIDA: A Azure subiu e o Java rodou! 🔥🔥🔥");

        // Simula um sucesso
        context.getLogger().info("Se você leu isso, a infraestrutura está perfeita.");
    }
}