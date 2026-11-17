# Social Relationship Tracking System

> **Note:** This repository is based on the project documentation detailed in the file **SocialRelationshipTracking_Report_v3.pdf**.

##  Introduction

The **Social Relationship Tracking System** is a Java Swing desktop application designed to provide fundamental social networking features. It allows users to register, log in, search for peers, manage friend requests, view profiles, and organize their friendships.

The application utilizes a MySQL database for robust data storage via JDBC, implementing the Data Access Object (DAO) pattern to ensure clean and organized code. Additionally, it features comprehensive activity logging and enables users to create and read binary backups of their friendship data.

##  Features

* **User Authentication & Profiles:** Register and log in as a standard or Premium User. Premium users benefit from extended profile details like a bio and verification badge.

* **Friendship Management:** Send, accept, or reject friend requests. Mark active friends as "close friends" and add personal notes to connections.

* **Relationship Archiving:** Safely remove friends without losing history; deleted friendships are automatically archived.

* **Search & Discovery:** Search for other users in the system (excluding Admin accounts).

* **Activity Logging:** Important user actions are simultaneously logged to the database and a local text file (`activity_log.txt`).

* **Data Backup:** Generate and restore binary backups of personal friendship data (`relationship_backup.dat`).

* **Role-Based Access:** Regular users can query their own data, while Admin users have access to system-wide query screens and comprehensive activity logs.

##  Technologies Used

* **Language:** Java (JDK)

* **GUI Framework:** Java Swing (`JFrame`)

* **Database:** MySQL

* **Database Connectivity:** JDBC

* **Architecture:** DAO (Data Access Object) Pattern, Object-Oriented Design

##  Database Schema

The system connects to a MySQL database named `social_relationship_db` consisting of five core tables:

1. **`users`**: Central table for all accounts (both regular and premium). Contains credentials, roles, and profile info.

2. **`friend_requests`**: Tracks all pending, accepted, and rejected requests between users.

3. **`friendships`**: Stores active relationships, including a `close_friend` toggle and custom notes.

4. **`old_friends`**: An archive table that preserves data of removed friendships.

5. **`activity_logs`**: Records significant user activities (e.g., logins, requests, profile updates) for auditing.

##  System Architecture

The application is structured into distinct layers to separate concerns:

* **Domain Model:** Represents core entities (`User`, `PremiumUser`, `FriendRequest`, `Friendship`). Utilizes polymorphic user reconstruction from the database.

* **Persistence Layer (DAO):** Handles all database interactions (`DatabaseConnection`, `UserDAO`, `FriendRequestDAO`, `FriendshipDAO`, `OldFriendDAO`, `ActivityLogDAO`).

* **Logging & Backup:** Dedicated managers (`LogManager`, `FileLogManager`, `BackupManager`) handle side-effects like file I/O and database logging.

* **UI Layer:** A suite of `JFrame` classes handling user interactions (e.g., `LoginFrame`, `MainMenuFrame`, `QueryFrame`).

##  Key Business Rules

The application enforces strict logic at the database and service levels:

1. **No Duplicate Usernames:** Profile updates and registrations are validated against existing records.

2. **No Self-Friend Requests:** A user cannot send a friend request to their own ID.

3. **No Redundant Requests:** Requests are blocked if a pending request already exists or if the users are already friends.

4. **Transactional Removals:** Removing a friend safely migrates the record to the `old_friends` table before deleting the active connection.

5. **Hidden Admins:** Admin accounts are excluded from user-facing search results.

6. **Role-Based Queries:** Standard users view personal data; administrators access system-wide query data.