package ru.practicum.shareit.booking.mappers;

import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class BookingMapper implements RowMapper<BookingDto> {

    @Override
    public BookingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookingDto  booking= new BookingDto();
        booking.setId(rs.getInt("id"));
        booking.setName(rs.getString("name"));
        booking.setStart(rs.getDate("start_date").toLocalDate());
        booking.setEnd(rs.getDate("end_date").toLocalDate());
        booking.setItemId(rs.getInt("item_id"));
        booking.setBookerId(rs.getInt("booker_id"));
        Status.valueOf(rs.getString("status"));

        return booking;
    }
}
