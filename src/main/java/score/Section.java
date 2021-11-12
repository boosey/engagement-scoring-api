package score;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;


@Entity
public class Section extends PanacheEntity {

  public String name;
  public Long weight;

  @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
  public List<PossibleResponse> possibleResponses = new ArrayList<PossibleResponse>();

  @ManyToOne
  @JoinColumn(name = "FK_TemplateId")
  private Template template;

  public void setTemplate(Template t) {
    this.template = t;
  }
}
