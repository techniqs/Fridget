package at.ac.tuwien.sepm.fridget.server.persistence;

import at.ac.tuwien.sepm.fridget.common.entities.Comment;

import java.util.List;

public interface CommentDAO {

    Comment createComment(int billId, Comment comment);

    List<Comment> findComments(int billId);

}
