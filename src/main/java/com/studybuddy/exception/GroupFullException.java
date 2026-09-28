package com.studybuddy.exception;

/** Business rule: a group cannot accept members beyond its capacity. -> 409 */
public class GroupFullException extends ConflictException {
    public GroupFullException(Long groupId, int capacity) {
        super("Study group " + groupId + " is full (capacity " + capacity + ")");
    }
}
