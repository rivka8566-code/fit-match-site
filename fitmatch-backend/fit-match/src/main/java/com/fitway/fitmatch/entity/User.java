package com.fitway.fitmatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // מציין שזו ישות שמתאימה לטבלה במסד הנתונים
@Table(name = "users") // שם הטבלה במסד הנתונים
@Data // גטרים וסטרים אוטומטיים
@NoArgsConstructor // קונסטרקטור ללא פרמטרים
@AllArgsConstructor // קונסטרקטור עם כל הפרמטרים
public class User {

    @Id // מציין שזהו המזהה הראשי של הישות
    @GeneratedValue(strategy = GenerationType.IDENTITY) // אסטרטגיית יצירת מזהה אוטומטית
    private Long id;

    @Column(nullable = false, unique = true)// עמודה שלא יכולה להיות ריקה וצריכה להיות ייחודית
    private String email;

    @Column(nullable = false)// עמודה שלא יכולה להיות ריקה
    private String password;

    @Column(nullable = false)// עמודה שלא יכולה להיות ריקה
    private String fullName;

    // נתוני ה-Dashboard וההתקדמות
    private int totalCaloriesBurned = 0; // סך כל הקלוריות שהוריד באתר
}