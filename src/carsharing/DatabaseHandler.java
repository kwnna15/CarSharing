/*package carsharing;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {


    void insert(){
        String insert = "INSERT INTO artists (name, origin, songs_number) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(insert)) {
            preparedStatement.setString(1, "The Beatles");
            preparedStatement.setString(2, "Liverpool, England");
            preparedStatement.setInt(3, 213);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}*/
