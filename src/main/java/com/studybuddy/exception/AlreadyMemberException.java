package com.studybuddy.exception;

/** Business rule: a student cannot join the same group twice. -> 409 */
public class AlreadyMemberException extends ConflictException {
    public AlreadyMemberException(Long studentId, Long groupId) {
        super("Student " + studentId + " is already a member of group " + groupId);
    }
}
