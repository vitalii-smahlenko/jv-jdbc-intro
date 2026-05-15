package mate.academy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import mate.academy.exception.DataProcessingException;
import mate.academy.model.Book;
import mate.utils.ConnectionUtil;

public class BookDaoImpl implements BookDao {

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO books (title, price) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setDouble(2, book.getPrice());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected < 1) {
                throw new DataProcessingException("Can't save book to DB, no rows affected");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getLong(1));
                } else {
                    throw new DataProcessingException("Can't save book to DB, no ID obtained");
                }
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Can't save book to DB");
        }
        return book;
    }

    @Override
    public Book get(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = new Book();
                    book.setId(resultSet.getLong("id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setPrice(resultSet.getDouble("price"));
                    return book;
                } else {
                    throw new DataProcessingException("Can't get book from DB, no book with id " + id);
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get book from DB");
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    @Override
    public Book update(Book book) {
        String sql = "UPDATE books SET title = ?, price = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setDouble(2, book.getPrice());
            statement.setLong(3, book.getId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected < 1) {
                throw new DataProcessingException("Can't update book in DB, no rows affected");
            }
            return book;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update book in DB");
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete book from DB");
        }
    }

}
