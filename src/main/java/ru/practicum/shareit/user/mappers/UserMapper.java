package ru.practicum.shareit.user.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<UserDto> {

    @Override
    public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDto userDto = new UserDto();
        userDto.setId(rs.getInt("id"));
        userDto.setName(rs.getString("name"));
        userDto.setEmail(rs.getString("email"));
        userDto.setActive(rs.getBoolean("active"));
        return userDto;
    }
}
