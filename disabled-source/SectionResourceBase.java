package score;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(hal = true, path = "section")
public interface SectionResourceBase extends PanacheEntityResource<Section, Long> {
  
}
