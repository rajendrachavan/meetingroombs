package neo.spring5.meetingroombooking.repositories;

import neo.spring5.meetingroombooking.models.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    Facility findFacilityById(Long id);
    Facility findByFacility(String facility_name);
}
