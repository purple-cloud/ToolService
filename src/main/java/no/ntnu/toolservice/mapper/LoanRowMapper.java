package no.ntnu.toolservice.mapper;

import no.ntnu.toolservice.domain.Loan;
import no.ntnu.toolservice.entity.Tool;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanRowMapper implements RowMapper<Loan> {

    @Override
    public Loan mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Loan(
                resultSet.getLong("tool_id"),
                resultSet.getString("name"),
                resultSet.getString("expiration_date"),
                resultSet.getLong("employee_id")
        );
    }

}
