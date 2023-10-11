package ru.practicum.shareit.item.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.item.dto.ItemDto;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ItemMapper implements RowMapper<ItemDto> {

    @Override
    public ItemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(rs.getInt("id"));
        itemDto.setName(rs.getString("name"));
        itemDto.setDescription(rs.getString("description"));
        itemDto.setAvailable(rs.getBoolean("available"));
        itemDto.setOwner(rs.getInt("owner"));
        itemDto.setRequestId(rs.getInt("request_id"));

        return itemDto;
    }
}
