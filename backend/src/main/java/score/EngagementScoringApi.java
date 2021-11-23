package score;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;

import io.quarkus.oidc.IdToken;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

@Path("/api")
public class EngagementScoringApi {

  @Inject
  ObjectMapper mapper;


  @Inject
  SecurityIdentity identity;

  @Inject
  @IdToken
  JsonWebToken idToken;

  @GET
  @Path("authenticated")
  @Produces("application/json")
  @NoCache
  public String authenticated() {
    // log.info("identity: {}", identity);
    return identity.toString();
  }


  @GET
  @Path("evaluation/{eid}")
  @Produces("application/json")
  @Transactional
  public Uni<Response> getItem(@PathParam("eid") Long eid) {

    return Uni.createFrom().item(ItemEvaluation.<ItemEvaluation>findById(eid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(eval -> {

        List<Long> selectedReponsesIds = eval.getSelectedResponses().stream().map(sr -> sr.id)
          .collect(Collectors.toList());

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("id", eval.id);
        rootNode.put("name", eval.name);
        rootNode.put("description", eval.description);
        rootNode.put("template_id", eval.getTemplate().id);
        ArrayNode sectionsArrayNode = rootNode.putArray("sections");

        List<Section> sections = eval.getTemplate().getSections();

        sections.forEach(section -> {
          ObjectNode sectionNode = sectionsArrayNode.addObject();
          sectionNode.put("id", section.id);
          sectionNode.put("name", section.name);
          ArrayNode possibleResponseArray = sectionNode.putArray("possibleResponses");

          section.getPossibleResponses().forEach(pr -> {
            ObjectNode possibleResponseNode = possibleResponseArray.addObject();
            possibleResponseNode.put("id", pr.id);
            possibleResponseNode.put("text", pr.text);
            possibleResponseNode.put("value", pr.value);
            possibleResponseNode.put("isSelected",
              selectedReponsesIds.contains(pr.id) ? "true" : "false");
          });
        });

        return okResponse(rootNode);
      });
  }


  // @Authenticated
  @GET
  @Path("template")
  @Blocking
  @Produces("application/json")
  public Uni<Response> listTemplates() {

    return Uni.createFrom().item(Response.ok(Template.<Template>listAll()).build())
      .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());

  }


  @GET
  @Path("template/{tid}")
  @Blocking
  @Produces("application/json")
  public Uni<Response> getTemplate(@PathParam("tid") Long tid) {

    return Uni.createFrom().item(Template.<Template>findById(tid))
      .onItem().ifNull()
      .failWith(new NotFoundException())
      .onItem().transform(t -> okResponse(t))
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

    return Uni.createFrom().item(Template.<Template>findById(tid))
      .onItem()
      .ifNull().failWith(new NotFoundException())
      .onItem()
      .<Response>transform(t -> {
        t.addSection(section);
        section.persist();
        return createdResponse("/template/%d/section/%d", tid, section.id);
      });
  }


  @GET
  @Path("template/{tid}/section/{sid}")
  @Blocking
  @Produces("application/json")
  public Uni<Response> getSection(@PathParam("tid") Long tid, @PathParam("sid") Long sid) {

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(s -> okResponse(s))
      .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
  }


  @Transactional
  @GET
  @Path("/template/{tid}/newEvaluation")
  @Produces("application/json")
  public Uni<Response> createNewEvaluation(@PathParam("tid") Long tid) {

    return Uni.createFrom().item(Template.<Template>findById(tid))
      .onItem().ifNull()
      .failWith(new NotFoundException())
      .onItem()
      .transform(t -> {
        ItemEvaluation eval = new ItemEvaluation();
        eval.name = "Untitled Evaluation";
        eval.setTemplate(t);
        eval.persist();
        ;
        return okResponse(eval);
      });
  }


  @Transactional
  @PATCH
  @Path("/evaluation")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> updateEvaluation(ItemEvaluation eval) {

    return Uni.createFrom().item(ItemEvaluation.<ItemEvaluation>findById(eval.id))
      .onItem().ifNull().failWith(new NotFoundException())
      .onItem().transform(e -> {

        e.name = eval.name;
        e.description = eval.description;

        return okResponse();
      });
  }


  // @Transactional
  // @POST
  // @Path("/template/{tid}/evaluation")
  // @Consumes("application/json")
  // @Produces("application/json")
  // public Uni<Response> createEvaluationFromTemplate(@PathParam("tid") Long tid,
  // ItemEvaluation eval) {

  // return
  // Uni.createFrom().item(Template.<Template>findById(tid)).onItem().ifNull()
  // .failWith(new NotFoundException()).onItem().<Response>transform(t -> {
  // t.addEvaluation(eval);
  // eval.persist();
  // return createdResponse("/template/%d/scores/item/%d", tid, eval.id);
  // });
  // }


  @Transactional
  @PATCH
  @Path("/evaluation/{eid}/section/{sid}/response/{rid}")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addResponseToEvaluation(@PathParam("eid") Long eid, @PathParam("sid") Long sid,
    @PathParam("rid") Long rid) {

    return Uni.combine().all()
      .unis(Uni.createFrom().item(ItemEvaluation.<ItemEvaluation>findById(eid)),
        Uni.createFrom().item(PossibleResponse.<PossibleResponse>findById(rid)),
        Uni.createFrom().item(Section.<Section>findById(sid)))
      .asTuple().onItem().transform(tuple -> {

        ItemEvaluation eval = tuple.getItem1();
        PossibleResponse newResponse = tuple.getItem2();
        Section section = tuple.getItem3();

        if (eval == null || section == null || newResponse == null) {
          throw new NotFoundException();
        }

        eval.addNewResponse(eval, section, newResponse);

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
      .failWith(new NotFoundException()).onItem()
      .transform(t -> Response.ok(t.getSections()).build());
  }


  @Transactional
  @POST
  @Path("/template/{tid}/section/{sid}/possible-response")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addResponseToSection(@PathParam("tid") Long tid, @PathParam("sid") Long sid,
    PossibleResponse possibleResponse) {

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().<Response>transform(s -> {
        s.addPossibleResponse(possibleResponse);
        possibleResponse.persist();

        return createdResponse("/template/%d/section/%d/possible-response/%d", tid, sid,
          possibleResponse.id);
      });
  }


  @GET
  @Path("template/{tid}/section/{sid}/possible-response/{rid}")
  @Blocking
  @Produces("application/json")
  public Uni<Response> getPossibleResponse(@PathParam("tid") Long tid, @PathParam("sid") Long sid,
    @PathParam("rid") Long rid) {

    return Uni.createFrom().item(PossibleResponse.<PossibleResponse>findById(rid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(r -> okResponse(r))
      .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
  }


  @Transactional
  @GET
  @Path("/template/{tid}/section/{sid}/possible-response")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> listPossibleResponses(@PathParam("tid") Long tid,
    @PathParam("sid") Long sid) {

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem()
      .transform(s -> Response.ok(s.getPossibleResponses()).build());

  }


  @Transactional
  @PATCH
  @Path("/template/{tid}/activate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> activateTemplate(@PathParam("tid") Long tid) {

    return Uni.createFrom().item(Template.<Template>findById(tid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(t -> {
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

    return Uni.createFrom().item(Template.<Template>findById(tid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(t -> {
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

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(s -> {
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

    return Uni.createFrom().item(Section.<Section>findById(sid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(s -> {
        s.deactivate();
        return okResponse();
      });
  }


  @Transactional
  @PATCH
  @Path("/template/{tid}/section/{sid}/possible-response/{pid}/activate")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> activatePossibleResponse(@PathParam("tid") Long tid,
    @PathParam("sid") Long sid, @PathParam("pid") Long pid) {

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
  public Uni<Response> deactivatePossibleResponse(@PathParam("tid") Long tid,
    @PathParam("sid") Long sid, @PathParam("pid") Long pid) {

    return Uni.createFrom().item(PossibleResponse.<PossibleResponse>findById(pid)).onItem().ifNull()
      .failWith(new NotFoundException()).onItem().transform(p -> {
        p.deactivate();
        return okResponse();
      });
  }


  private Response okResponse() {
    return Response.ok().build();
  }


  private Response okResponse(Object payload) {
    return Response.ok(payload).build();
  }


  private Response createdResponse(String s, Object... args) {
    return Response.created(URI.create(String.format(s, args))).build();
  }


  private Uni<Response> createdResponseUni(String s, Object... args) {
    return Uni.createFrom().item(createdResponse(s, args));
  }
}
