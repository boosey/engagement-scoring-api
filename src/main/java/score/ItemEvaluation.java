package score;

import java.util.List;

import javax.persistence.JoinColumn;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public class ItemEvaluation extends PanacheEntity {

  @OneToOne
  @JoinColumn(name = "FK_TemplateId")
  public Template template;

  @OneToMany
  public List<PossibleResponse> selectedResponses;
  
}