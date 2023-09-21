package ru.practicum.shareit.request.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemRequestMapper implements RowMapper<ItemRequestDto> {
    @Override
    public ItemRequestDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(rs.getInt("id"));
        itemRequestDto.setDescription(rs.getString("description"));
        itemRequestDto.setRequestor(rs.getInt("requestor"));
        itemRequestDto.setCreated(rs.getDate("created_date").toLocalDate());

        return itemRequestDto;
    }

}
