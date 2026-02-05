package com.edu.edupage.repository;

import com.edu.edupage.entity.Schedule;
import com.edu.edupage.entity.ClassGroup;
import com.edu.edupage.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
        List<Schedule> findByClassGroup(ClassGroup classGroup);

        List<Schedule> findByClassGroupId(Long classGroupId);

        List<Schedule> findByClassroom_Id(Long classroomId);

        List<Schedule> findByTeacher(Teacher teacher);

        List<Schedule> findByTeacherId(Long teacherId);

        List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);

        @Query("SELECT s FROM Schedule s WHERE s.classGroup.id = :classGroupId ORDER BY s.dayOfWeek, s.startTime")
        List<Schedule> findWeeklyScheduleByClassGroup(@Param("classGroupId") Long classGroupId);

        @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId ORDER BY s.dayOfWeek, s.startTime")
        List<Schedule> findWeeklyScheduleByTeacher(@Param("teacherId") Long teacherId);

        @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId AND s.dayOfWeek = :dayOfWeek " +
                        "AND ((s.startTime <= :startTime AND s.endTime > :startTime) OR " +
                        "(s.startTime < :endTime AND s.endTime >= :endTime) OR " +
                        "(s.startTime >= :startTime AND s.endTime <= :endTime))")
        List<Schedule> findConflictingTeacherSchedules(
                        @Param("teacherId") Long teacherId,
                        @Param("dayOfWeek") DayOfWeek dayOfWeek,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

        @Query("SELECT s FROM Schedule s WHERE s.classGroup.id = :classGroupId AND s.dayOfWeek = :dayOfWeek " +
                        "AND ((s.startTime <= :startTime AND s.endTime > :startTime) OR " +
                        "(s.startTime < :endTime AND s.endTime >= :endTime) OR " +
                        "(s.startTime >= :startTime AND s.endTime <= :endTime))")
        List<Schedule> findConflictingClassSchedules(
                        @Param("classGroupId") Long classGroupId,
                        @Param("dayOfWeek") DayOfWeek dayOfWeek,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

        @Query("SELECT s FROM Schedule s WHERE s.classroom.id = :classroomId AND s.dayOfWeek = :dayOfWeek " +
                        "AND ((s.startTime <= :startTime AND s.endTime > :startTime) OR " +
                        "(s.startTime < :endTime AND s.endTime >= :endTime) OR " +
                        "(s.startTime >= :startTime AND s.endTime <= :endTime))")
        List<Schedule> findConflictingClassroomSchedules(
                        @Param("classroomId") Long classroomId,
                        @Param("dayOfWeek") DayOfWeek dayOfWeek,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);
}
