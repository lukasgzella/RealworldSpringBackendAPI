package com.hibernateRealworldRelations.realworldRelations.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MultipleCommentResponse {

    private List<CommentResponse> comments;

    @Override
    public String toString() {
        return "MultipleCommentResponse{" +
                "comments=" + comments.toString() +
                '}';
    }
}