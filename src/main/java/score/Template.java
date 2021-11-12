package score;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;




@Entity
public class Template extends PanacheEntity {

  public String name;

  @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
  public List<Section> sections;
}
