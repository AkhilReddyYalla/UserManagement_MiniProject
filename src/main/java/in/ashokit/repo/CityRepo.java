package in.ashokit.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.ashokit.entity.CityEntity;

public interface CityRepo extends JpaRepository<CityEntity, Integer> {

    @Query("SELECT c FROM CityEntity c WHERE c.state.stateId = :stateId")
    public List<CityEntity> getCities(@Param("stateId") Integer stateId);

}
