package score;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;


@Entity
public class Section extends PanacheEntity {

  public String name;
  public Long weight;

  @OneToMany(mappedBy = "section", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<PossibleResponse> possibleResponses = new ArrayList<PossibleResponse>();

  public List<PossibleResponse> getPossibleResponses() {
    return possibleResponses;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "FK_TemplateId")
  private Template template;


  public void setTemplate(Template t) {
    this.template = t;
  }

  public void addPossibleResponse(PossibleResponse possibleResponse) {
		possibleResponses.add( possibleResponse );
		possibleResponse.setSection( this );
	}

	public void removePossibleResponse(PossibleResponse possibleResponse) {
		possibleResponses.remove( possibleResponse );
		possibleResponse.setSection( null );
	}

}
