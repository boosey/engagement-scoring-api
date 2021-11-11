package score;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(hal = true, path = "possible-response")
public interface PossibleResponseResourceBase extends PanacheEntityResource<PossibleResponse, Long> {
  
}
