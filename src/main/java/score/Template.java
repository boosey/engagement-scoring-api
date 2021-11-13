package score;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;




@Entity
public class Template extends PanacheEntity {

  public String name;

  @OneToMany(mappedBy = "template", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  public List<Section> sections = new ArrayList<Section>();

  public void addSection(Section section) {
		sections.add( section );
		section.setTemplate( this );
	}

	public void removeSection(Section section) {
		sections.remove( section );
		section.setTemplate( null );
	}
}
