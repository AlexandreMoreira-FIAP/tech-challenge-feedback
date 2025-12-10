package br.com.fiap.adapter.driver.api.controller;

import br.com.fiap.adapter.driven.infra.database.FeedbackRepositoryAdapter;
import br.com.fiap.adapter.driver.api.request.FeedbackRequest;
import br.com.fiap.core.domain.model.Feedback;
import br.com.fiap.core.usecase.CriarFeedbackUseCase;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/avaliacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeedbackController {

    private final CriarFeedbackUseCase useCase;
    private final FeedbackRepositoryAdapter repositoryAdapter;

    public FeedbackController(FeedbackRepositoryAdapter repositoryAdapter) {
        this.repositoryAdapter = repositoryAdapter;
        this.useCase = new CriarFeedbackUseCase(repositoryAdapter);
    }

    @POST
    public Response criar(FeedbackRequest request) {
        try {
            Feedback feedback = new Feedback(request.descricao, request.nota);

            Feedback salvo = useCase.executar(feedback);

            return Response.status(Response.Status.CREATED).entity(salvo).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public List<Feedback> listar() {
        return repositoryAdapter.listarTodos();
    }
}