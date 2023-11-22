package com.hibernateRealworldRelations.realworldRelations.dto.responses;

import com.hibernateRealworldRelations.realworldRelations.dto.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private long id;
    private String createdAt;
    private String updatedAt;
    private String body;
    private Author author;

    @Override
    public String toString() {
        return "CommentResponse{" +
                "id=" + id +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", body='" + body + '\'' +
                ", author=" + author +
                '}';
    }
}