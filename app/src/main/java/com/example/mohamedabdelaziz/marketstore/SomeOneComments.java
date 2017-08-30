package com.example.mohamedabdelaziz.marketstore;

/**
 * Created by Mohamed Abd Elaziz on 8/15/2017.
 */

class SomeOneComments {
    private String postID, commentID, Name, Comment;

    public SomeOneComments(String postID, String commentID, String name, String comment) {
        this.postID = postID;
        Name = name;
        Comment = comment;
        this.commentID = commentID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
