package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}

/*
* 실무에서는 이렇게 사용하면 안되고 별도의 jdbc를 사용해야 함
* LockRepository가 Jpa 데이터 소스를 활용함으로 커넥션풀을 모두 사용하게 되면 서비스에 영향을 미칠 수 있어 jdbc를 통해 별도로 락을 걸도록 해서 본래 서비스에 영향을 주지 않도록
* */

