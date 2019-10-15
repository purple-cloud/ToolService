package no.ntnu.toolservice.repository;

import no.ntnu.toolservice.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToolRepository extends JpaRepository<Tool, Long> {

    Tool findByToolId(Long toolId);

}
