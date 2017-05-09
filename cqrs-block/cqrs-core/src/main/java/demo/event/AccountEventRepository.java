package demo.event;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountEventRepository extends PagingAndSortingRepository<AccountEvent, Long> {
    List<AccountEvent> findEventsByAccountId(@Param("accountId") Long accountId);
}
