# Edupage Mobile App - Figma Design Specifications

## 📋 Project Overview

Create a modern, professional mobile application UI/UX design for **Edupage** - a comprehensive School Management System. The app serves three user types: **Students**, **Teachers**, and **Administrators**.

---

## 🎨 Color Palette

### Primary Colors (Mandatory)
| Name | Hex Code | Usage |
|------|----------|-------|
| **Pure White** | `#FFFFFF` | Backgrounds, cards, input fields |
| **Deep Navy** | `#2D2652` | Headers, primary buttons, navigation, text |
| **Accent Red** | `#D11021` | Alerts, notifications, important actions, badges |

### Secondary Colors (Complementary)
| Name | Hex Code | Usage |
|------|----------|-------|
| **Light Navy** | `#4A4270` | Secondary buttons, hover states |
| **Soft Gray** | `#F5F5F7` | Page backgrounds, dividers |
| **Medium Gray** | `#8E8E93` | Placeholder text, disabled states |
| **Dark Gray** | `#3A3A3C` | Body text, descriptions |
| **Success Green** | `#34C759` | Success states, present attendance |
| **Warning Amber** | `#FF9500` | Warnings, late attendance |
| **Light Red** | `#FFE5E7` | Error backgrounds, absent highlight |
| **Light Blue** | `#E8E6F0` | Selected states, highlights |

---

## 📐 Typography

### Font Family
**Primary**: Inter (or SF Pro for iOS feel)
**Fallback**: System default sans-serif

### Type Scale
| Style | Size | Weight | Line Height | Usage |
|-------|------|--------|-------------|-------|
| H1 - Hero | 32px | Bold (700) | 40px | Welcome screens, main titles |
| H2 - Title | 24px | SemiBold (600) | 32px | Page titles, section headers |
| H3 - Subtitle | 20px | SemiBold (600) | 28px | Card titles, modal headers |
| Body Large | 17px | Regular (400) | 24px | Primary content |
| Body | 15px | Regular (400) | 22px | Standard text, descriptions |
| Caption | 13px | Regular (400) | 18px | Labels, timestamps, hints |
| Small | 11px | Medium (500) | 14px | Badges, tags, metadata |

---

## 📏 Spacing & Layout

### Grid System
- **Screen width**: 375px (iPhone standard) - design at 1x
- **Content margins**: 16px left/right
- **Card padding**: 16px internal
- **Section spacing**: 24px between sections
- **Item spacing**: 12px between list items

### Border Radius
| Element | Radius |
|---------|--------|
| Buttons | 12px |
| Cards | 16px |
| Input fields | 10px |
| Avatars | 50% (circular) |
| Tags/Badges | 8px |
| Modal/Bottom sheets | 24px (top corners) |

### Shadows
```
Card Shadow: 0px 2px 8px rgba(45, 38, 82, 0.08)
Modal Shadow: 0px -4px 20px rgba(45, 38, 82, 0.15)
Button Shadow: 0px 4px 12px rgba(45, 38, 82, 0.20)
```

---

## 📱 Screen Designs Required

### 1. AUTHENTICATION FLOW

#### 1.1 Splash Screen
- Full-screen Deep Navy (#2D2652) background
- Centered Edupage logo (white)
- Subtle loading indicator
- School motto/tagline in white at bottom

#### 1.2 Login Screen
**Layout:**
- White background
- Logo at top (20% from top)
- "Welcome Back" title in Deep Navy
- "Sign in to continue" subtitle in Medium Gray
- Email input field with mail icon
- Password input field with lock icon and show/hide toggle
- "Forgot Password?" link in Deep Navy (right-aligned)
- Primary button "Sign In" (Deep Navy, full width)
- Divider "or continue with"
- Social login buttons (Google, Apple) - outlined style
- "Don't have an account? Contact Admin" at bottom

**Input Field Styling:**
- Border: 1px solid #E5E5EA (default)
- Border: 2px solid #2D2652 (focused)
- Border: 2px solid #D11021 (error)
- Background: #FFFFFF
- Placeholder: Medium Gray
- Error message below field in red

#### 1.3 Forgot Password Screen
- Back arrow navigation
- Illustration of email/lock
- Title: "Reset Password"
- Description text
- Email input field
- "Send Reset Link" primary button
- Success state with checkmark animation

#### 1.4 Registration Screen (Admin-initiated)
- Similar to login layout
- Fields: First Name, Last Name, Email, Password, Confirm Password
- Role selector (dropdown or segmented control)
- Class assignment (for students)
- Subject selection (for teachers)
- "Create Account" primary button

---

### 2. STUDENT DASHBOARD

#### 2.1 Home Screen (Student)
**Header:**
- Deep Navy background with subtle gradient
- Greeting: "Good Morning, [First Name]" in white
- Profile avatar (top right)
- Today's date below greeting
- Notification bell with red badge

**Today's Schedule Card:**
- White card with shadow
- "Today's Classes" title
- Timeline view of classes with:
  - Time (left column)
  - Subject name (bold)
  - Teacher name (caption)
  - Room number (badge)
- Current class highlighted with Light Blue background
- "View Full Schedule" link

**Quick Stats Row (Horizontal scroll):**
- Attendance percentage (circular progress, green/yellow/red)
- Average grade
- Pending assignments count
- Upcoming exams count

**Recent Grades Section:**
- Last 3-4 grades as cards
- Subject icon, grade value, date
- Color-coded (green for good, amber for average, red for poor)

**Announcements Preview:**
- 2 latest announcements
- Important ones with red badge
- "See All" link

**Bottom Navigation (Tab Bar):**
- Deep Navy background
- Icons: Home, Schedule, Grades, Profile
- Active icon in white with indicator
- Inactive icons in Light Navy

#### 2.2 Schedule Screen
**Views (Segmented Control):**
- Day View (default)
- Week View
- Month View

**Day View:**
- Date selector at top (swipeable)
- Vertical timeline
- Each class as a card:
  - Time range
  - Subject (large)
  - Teacher avatar + name
  - Room with location icon
  - Tap to see details

**Week View:**
- Grid layout
- Days as columns
- Time slots as rows
- Color-coded by subject
- Compact class cards

#### 2.3 Grades Screen
**Header:**
- Overall GPA/Average display (large)
- Semester selector dropdown

**Subject List:**
- Expandable accordion per subject
- Subject icon and name
- Current average (colored indicator)
- Expand to see individual grades:
  - Grade type (Quiz, Exam, Homework)
  - Score / Max score
  - Date
  - Teacher who graded

**Filters:**
- Filter by subject
- Filter by grade type
- Date range

#### 2.4 Attendance Screen
**Monthly Calendar View:**
- Calendar with colored dots:
  - Green dot: Present
  - Red dot: Absent
  - Amber dot: Late
  - Gray dot: Excused
- Legend at bottom

**Statistics:**
- Attendance percentage (circular chart)
- Present/Absent/Late/Excused counts
- Trend arrow (improving/declining)

**Detailed List:**
- Expandable by date
- Shows each class with status icon

---

### 3. TEACHER DASHBOARD

#### 3.1 Home Screen (Teacher)
**Header (similar to student):**
- Different greeting context
- Quick action buttons: "Take Attendance", "Add Grade"

**Today's Classes:**
- Classes to teach today
- Class name, Subject, Time, Room
- Student count per class

**Pending Tasks:**
- Assignments to grade count
- Attendance to mark

**My Students Overview:**
- Classes assigned
- Total student count

#### 3.2 Class Management Screen
**Class List:**
- All assigned classes as cards
- Grade level, Section name
- Student count
- Subject taught

**Tap on Class → Class Detail:**
- Student roster
- Search students
- Bulk actions (attendance, announcements)

#### 3.3 Attendance Taking Screen
**Header:**
- Class name, Subject, Date
- Quick select all "Present" button

**Student List:**
- Student photo, name
- Status buttons: Present (green), Absent (red), Late (amber), Excused (gray)
- Notes field (expandable)
- Swipe actions

**Submit Button:**
- Fixed at bottom
- Shows count: "Submit (25 students)"

#### 3.4 Grade Entry Screen
**Select Context:**
- Choose Class → Subject → Grade Type

**Grade Form:**
- Assignment/Test name
- Max score
- Date

**Student Grade List:**
- Student name
- Number input for score
- Optional comment
- Batch entry mode

---

### 4. ADMIN DASHBOARD

#### 4.1 Admin Home
**Overview Cards:**
- Total Users
- Total Students
- Total Teachers
- Total Classes

**Quick Actions Grid:**
- Add User
- Manage Classes
- Create Announcement
- View Reports

**Recent Activity Feed:**
- Latest registrations
- System events

#### 4.2 User Management
**Tab Bar:**
- All Users, Students, Teachers, Admins

**User List:**
- Avatar, Name, Email, Role badge
- Search bar with filters
- FAB for "Add User"

**User Detail/Edit:**
- Full profile form
- Role assignment
- Class assignment (students)
- Subject assignment (teachers)
- Account status toggle

#### 4.3 Announcement Creation
**Form:**
- Title (required)
- Content (rich text area)
- Target audience:
  - Everyone
  - Teachers only
  - Students only
  - Specific class
- Priority toggle (Important)
- Schedule publish date
- Expiry date

**Preview Mode:**
- See how it will appear

---

### 5. COMMON COMPONENTS

#### 5.1 Profile Screen
- Large avatar (editable)
- Name, Role badge
- Email (non-editable)
- Student ID / Employee ID
- Class (for students)
- Subjects (for teachers)
- Settings link
- Logout button (outlined, red)

#### 5.2 Settings Screen
- Notifications preferences
- Theme (Light/Dark/System)
- Language
- Change Password
- Privacy Policy
- About App
- App Version

#### 5.3 Notification Center
- List of notifications
- Unread indicator
- Grouped by date
- Swipe to dismiss
- Mark all as read

#### 5.4 Empty States
Design empty states for:
- No classes today
- No grades yet
- No announcements
- No search results
(Use illustrations with Deep Navy accents)

#### 5.5 Loading States
- Skeleton loaders for lists/cards
- Subtle shimmer animation
- Branded loading spinner (Deep Navy)

#### 5.6 Error States
- Connection error
- Server error
- Permission denied
(Use illustrations with Red accents)

---

## 🎯 Interaction Guidelines

### Buttons
| Type | Background | Text | Border |
|------|------------|------|--------|
| Primary | #2D2652 | #FFFFFF | None |
| Secondary | #FFFFFF | #2D2652 | 1px #2D2652 |
| Destructive | #D11021 | #FFFFFF | None |
| Ghost | Transparent | #2D2652 | None |
| Disabled | #E5E5EA | #8E8E93 | None |

### Button States
- Default: Base styling
- Pressed: Darken 10%
- Disabled: Gray out
- Loading: Show spinner, disable

### Micro-interactions
- Button tap: Scale down to 0.98
- Card tap: Subtle lift shadow
- Tab switch: Slide transition
- Success: Checkmark animation
- Error: Shake animation
- Pull to refresh: Custom branded animation

---

## 📐 Figma File Structure

```
📁 Edupage Mobile App
├── 📄 Cover
├── 📁 🎨 Design System
│   ├── Colors
│   ├── Typography
│   ├── Icons
│   ├── Components
│   └── Patterns
├── 📁 📱 Screens - Authentication
│   ├── Splash
│   ├── Login
│   ├── Forgot Password
│   └── Registration
├── 📁 📱 Screens - Student
│   ├── Home
│   ├── Schedule
│   ├── Grades
│   ├── Attendance
│   └── Profile
├── 📁 📱 Screens - Teacher
│   ├── Home
│   ├── Classes
│   ├── Take Attendance
│   ├── Enter Grades
│   └── Profile
├── 📁 📱 Screens - Admin
│   ├── Dashboard
│   ├── User Management
│   ├── Announcements
│   └── Settings
├── 📁 ✨ Prototypes
│   ├── Login Flow
│   ├── Student Journey
│   ├── Teacher Journey
│   └── Admin Journey
└── 📁 📦 Assets
    ├── Logo
    ├── Illustrations
    └── Icons
```

---

## ✅ Deliverables Checklist

- [ ] Complete Design System with all tokens
- [ ] All authentication screens (4 screens)
- [ ] Student app screens (5+ screens)
- [ ] Teacher app screens (5+ screens)
- [ ] Admin app screens (4+ screens)
- [ ] All component variants
- [ ] Empty, loading, and error states
- [ ] Interactive prototype for each user flow
- [ ] Dark mode variants (optional but recommended)
- [ ] Tablet/iPad adaptations (optional)

---

## 🚀 Design Principles

1. **Clarity First**: Information hierarchy should be immediately clear
2. **Consistent**: Same patterns and components throughout
3. **Accessible**: Minimum 4.5:1 contrast ratio, touch targets 44px+
4. **Efficient**: Minimize taps to complete common tasks
5. **Delightful**: Subtle animations that enhance without distracting
6. **Professional**: Clean, modern aesthetic suitable for educational context

---

## 💡 Pro Tips for Figma

1. Use **Auto Layout** for all components for responsive behavior
2. Create **Component Sets** with variants (state, size, type)
3. Use **Local Variables** for colors and spacing for easy theming
4. Set up **Prototype Connections** between frames
5. Add **Smart Animate** for smooth transitions
6. Include **Device Frames** for presentation
7. Use **Figma Sections** to organize the file
8. Add **Dev Mode** annotations for developers

---

*This design specification is for the Edupage School Management Mobile Application. Follow these guidelines to create a cohesive, professional, and user-friendly interface.*
