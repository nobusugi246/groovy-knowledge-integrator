package gki.container.domain

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface BotRepository extends PagingAndSortingRepository<Bot, Long> {
    List<Bot> findByName(@Param("name") String name);
}
