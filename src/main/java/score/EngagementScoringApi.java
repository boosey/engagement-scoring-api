package score;

import java.net.URI;
import java.util.List;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

@Path("/api")
public class EngagementScoringApi {

  @GET
  @Path("template")
  @Blocking
  public Uni<List<Template>> listTemplates() {
  
    return Uni.createFrom().item(Template.<Template>listAll()).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());

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

    Template t = Template.findById(tid);

    if (t != null) {
      section.setTemplate(t);
      section.persist();

      t.sections.add(section);

      return createdResponseUni("/template/%d/section/%d", tid, section.id);
    } else {
      return notFoundResponseUni();
    }
  }


  @Transactional
  @GET
  @Path("/template/{tid}/section")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> listSections(@PathParam("tid") Long tid) {

    return Uni.createFrom()
      .<Template>item(Template.findById(tid))
      .onItem()
      .ifNull().failWith(new NotFoundException())
      .onItem()
      .transform(t -> Response.ok(t.sections).build());
    
  }

  
  @Transactional
  @POST
  @Path("/template/{tid}/section/{sid}/possible-reponse")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> addResponseToSection(@PathParam("tid") Long tid, @PathParam("sid") Long sid,
      PossibleResponse possibleResponse) {

    Section s = Section.findById(sid);

    if (s != null) {

      possibleResponse.setSection(s);
      possibleResponse.persist();

      s.possibleResponses.add(possibleResponse);

      return createdResponseUni("/template/%d/section/%d/possible-response/%d", tid, sid, possibleResponse.id);

    } else {
      return notFoundResponseUni();
    }
  }

  @Transactional
  @GET
  @Path("/template/{tid}/section/{sid}/possible-response")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<List<PossibleResponse>> listPossibleResponses(@PathParam("tid") Long tid, @PathParam("sid") Long sid) {

    Section s = Section.findById(sid);

    return Uni.createFrom().item(s.possibleResponses).onItem().ifNull().failWith((Throwable) notFoundResponseUni());
  }

  @Transactional
  @DELETE
  @Path("/template/{tid}")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> deleteTemplate(@PathParam("tid") Long tid) {

    Template t = Template.findById(tid);

    if (t != null) {
      t.delete();
      return createdResponseUni("/template/%d", t.id);
    } else {
      return notFoundResponseUni();
    }
  }

  @Transactional
  @DELETE
  @Path("/template/{tid}/section/{sid}")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> deleteSection(@PathParam("tid") Long tid, @PathParam("sid") Long sid) {

    Section s = Section.findById(sid);

    if (s != null) {
      s.delete();
      return createdResponseUni("/template/%d/section/%d", tid, s.id);
    } else {
      return notFoundResponseUni();
    }
  }

  @Transactional
  @DELETE
  @Path("/template/{tid}/section/{sid}/possible-response/{pid}")
  @Consumes("application/json")
  @Produces("application/json")
  public Uni<Response> deletePossibleResponse(@PathParam("tid") Long tid, @PathParam("sid") Long sid,
      @PathParam("pid") Long pid) {

    PossibleResponse p = PossibleResponse.findById(pid);

    if (p != null) {
      p.delete();
      return createdResponseUni("/template/%d/section/%d/possible-response/%d", tid, sid, p.id);
    } else {
      return notFoundResponseUni();
    }
  }

  
  
  private Response createdResponse(String s, Object... args) {
    return Response.created(URI.create(String.format(s, args))).build();
  }
  
  private Response notFoundResponse() {
    return Response.status(Status.NOT_FOUND).build();
  } 
    
  private Uni<Response> createdResponseUni(String s, Object... args) {
    return Uni.createFrom().item(createdResponse(s, args));
  }
  
  private Uni<Response> notFoundResponseUni() {
    return Uni.createFrom().item(notFoundResponse());
  } 
  
}
