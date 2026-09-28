package com.studybuddy.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class Dtos {

    private Dtos() {}

    // ---------- requests (validated) ----------
    public record SubjectRequest(
            @NotBlank(message = "name is required") @Size(max = 100) String name,
            @Size(max = 255) String description) {}

    public record StudentRequest(
            @NotBlank(message = "name is required") @Size(max = 100) String name,
            @NotBlank(message = "email is required") @Email(message = "must be a valid email") @Size(max = 150) String email) {}

    public record CreateGroupRequest(
            @NotBlank(message = "name is required") @Size(max = 100) String name,
            @NotNull(message = "subjectId is required") @Positive Long subjectId,
            @NotNull(message = "creatorId is required") @Positive Long creatorId,
            @NotNull(message = "capacity is required") @Positive(message = "capacity must be greater than 0")
            @Max(value = 100, message = "capacity cannot exceed 100") Integer capacity) {}

    public record UpdateGroupRequest(
            @NotNull(message = "requesterId is required") @Positive Long requesterId,
            @NotBlank(message = "name is required") @Size(max = 100) String name,
            @NotNull(message = "capacity is required") @Positive(message = "capacity must be greater than 0")
            @Max(value = 100, message = "capacity cannot exceed 100") Integer capacity) {}

    public record JoinRequest(
            @NotNull(message = "studentId is required") @Positive Long studentId) {}

    // ---------- responses ----------
    public record SubjectResponse(Long id, String name, String description) {}

    public record StudentResponse(Long id, String name, String email) {}

    public record GroupResponse(
            Long id, String name,
            Long subjectId, String subjectName,
            Long creatorId, String creatorName,
            int capacity, long currentMembers, int seatsLeft, boolean full,
            LocalDateTime createdAt) {}

    public record MemberResponse(
            Long studentId, String name, String email,
            LocalDateTime joinedAt, boolean creator) {}
}
