package score;

import java.net.URI;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

@Path("/api")
public class EngagementScoringApi {

  @GET
  @Path("template")
  @Blocking
  public Uni<Response> listTemplates() {

    return Uni.createFrom().item(Response.ok(Template.<Template>listAll()).build())
        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());

  }

  @Transactional
  @POST
  @Path("template")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addTemplate(Template template) {

    template.persist();
    return createdResponseUni("/template/%d", template.id);

  }

  @Transactional
  @POST
  @Path("/template/{tid}/section")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addSectionToTemplate(@PathParam("tid") Long tid, Section section) {

    return Uni.createFrom().item(Template.<Template>findById(tid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().<Response>transform(t -> {
          t.addSection(section);
          section.persist();
          return createdResponse("/template/%d/section/%d", tid, section.id);
        });
  }

  @Transactional
  @POST
  @Path("/template/{tid}/scores/item")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addItemScoreToTemplate(@PathParam("tid") Long tid, ItemEvaluation eval) {

    return Uni.createFrom().item(Template.<Template>findById(tid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().<Response>transform(t -> {
          t.addEvaluation(eval);
          eval.persist();
          return createdResponse("/template/%d/scores/item/%d", tid, eval.id);
        });
  }

  @Transactional
  @PATCH
  @Path("/template/{tid}/scores/item/{iid}/section/{sid}/response/{rid}")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addResponseToEvaluation(@PathParam("tid") Long tid, @PathParam("iid") Long iid,
      @PathParam("sid") Long sid, @PathParam("rid") Long rid) {

    return Uni.combine().all()
        .unis(Uni.createFrom().item(ItemEvaluation.<ItemEvaluation>findById(iid)),
            Uni.createFrom().item(PossibleResponse.<PossibleResponse>findById(rid)))
        .asTuple().onFailure().transform(ex -> new NotFoundException()).onItem().transform(tuple -> {
          tuple.getItem1().addResponse(tuple.getItem2());
          return okResponse();
        });
  }

  @Transactional
  @GET
  @Path("/template/{tid}/section")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> listSections(@PathParam("tid") Long tid) {

    return Uni.createFrom().<Template>item((Template.<Template>findById(tid))).onItem().ifNull()
        .failWith(new NotFoundException()).onItem().transform(t -> Response.ok(t.sections).build());
  }

  @Transactional
  @POST
  @Path("/template/{tid}/section/{sid}/possible-response")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addResponseToSection(@PathParam("tid") Long tid, @PathParam("sid") Long sid,
      PossibleResponse possibleResponse) {

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().<Response>transform(s -> {
          s.addPossibleResponse(possibleResponse);
          possibleResponse.persist();

          return createdResponse("/template/%d/section/%d/possible-response/%d", tid, sid, possibleResponse.id);
        });
  }

  @Transactional
  @GET
  @Path("/template/{tid}/section/{sid}/possible-response")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> listPossibleResponses(@PathParam("tid") Long tid, @PathParam("sid") Long sid) {

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().transform(s -> Response.ok(s.getPossibleResponses()).build());

  }

  @Transactional
  @PATCH
  @Path("/template/{tid}/activate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> activateTemplate(@PathParam("tid") Long tid) {

    return Uni.createFrom().item(Template.<Template>findById(tid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().transform(t -> {
          t.activate();
          return okResponse();
        });
  }

  @Transactional
  @PATCH
  @Path("/template/{tid}/deactivate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> deactivateTemplate(@PathParam("tid") Long tid) {

    return Uni.createFrom().item(Template.<Template>findById(tid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().transform(t -> {
          t.deactivate();
          return okResponse();
        });
  }

  @Transactional
  @PATCH
  @Path("/template/{tid}/section/{sid}/activate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> activateSection(@PathParam("tid") Long tid, @PathParam("sid") Long sid) {

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().transform(s -> {
          s.activate();
          return okResponse();
        });
  }

  @Transactional
  @PATCH
  @Path("/template/{tid}/section/{sid}/deactivate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> deactivateSection(@PathParam("tid") Long tid, @PathParam("sid") Long sid) {

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull().failWith(new NotFoundException())
        .onItem().transform(s -> {
          s.deactivate();
          return okResponse();
        });
  }

  @Transactional
  @PATCH
  @Path("/template/{tid}/section/{sid}/possible-response/{pid}/activate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> activatePossibleResponse(@PathParam("tid") Long tid, @PathParam("sid") Long sid,
      @PathParam("pid") Long pid) {

    return Uni.createFrom().item(PossibleResponse.<PossibleResponse>findById(pid)).onItem().ifNull()
        .failWith(new NotFoundException()).onItem().transform(p -> {
          p.activate();
          return okResponse();
        });
  }

  @Transactional
  @PATCH
  @Path("/template/{tid}/section/{sid}/possible-response/{pid}/deactivate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> deactivatePossibleResponse(@PathParam("tid") Long tid, @PathParam("sid") Long sid,
      @PathParam("pid") Long pid) {

    return Uni.createFrom().item(PossibleResponse.<PossibleResponse>findById(pid)).onItem().ifNull()
        .failWith(new NotFoundException()).onItem().transform(p -> {
          p.deactivate();
          return okResponse();
        });
  }

  private Response okResponse() {
    return Response.ok().build();
  }

  private Response createdResponse(String s, Object... args) {
    return Response.created(URI.create(String.format(s, args))).build();
  }

  // private Response deletedResponse(Long count) {
  // return Response.ok(count).build();
  // }

  private Uni<Response> createdResponseUni(String s, Object... args) {
    return Uni.createFrom().item(createdResponse(s, args));
  }
}
