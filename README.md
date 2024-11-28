# RAF Vacuum Control

A web application simulating the management of robotic vacuum cleaners. This application enables users to efficiently add, control, schedule operations, and track errors for robotic vacuums in a multi-user system. Each user manages their own vacuums with specific permissions.

# Technologies
- **Backend**: Spring Boot Java 
- **Database**: MySQL  
- **Programming Language**: Java  
- **Frameworks**: Spring Boot, Maven  

## Functionalities

This application provides several functionalities for managing robotic vacuum cleaners:

### 1. User Management
- Add, retrieve, and manage users.
- Permissions system to restrict or grant access to specific vacuum operations.

### 2. Vacuum Management
- Add new vacuums with required details (name, type, description, etc.).
- View all active vacuums added by the logged-in user.
- Search for vacuums by:
  - Name (case insensitive).
  - Status (ON, OFF, DISCHARGING).
  - Creation date range (using `dateFrom` and `dateTo` parameters).
- Remove vacuums (mark as removed but retain in the database).

### 3. Vacuum Operations
- **START**: Begin operation; only available if the vacuum is in the STOPPED state. Transition to RUNNING after a delay.  
- **STOP**: Stop operation; only available if the vacuum is in the RUNNING state. Transition to STOPPED after a delay.  
- **DISCHARGE**: Empty the vacuum; only available if the vacuum is in the STOPPED state. Transition to DISCHARGING and then back to STOPPED.  
- Operations (START, STOP, DISCHARGE) can be scheduled for execution at a specified date and time.

### 4. Automatic Features
- After three RUNNING-STOPPED cycles, the vacuum is automatically discharged, even if the user lacks the necessary permissions.  

### 5. Error Tracking
- Failed scheduled operations are recorded in an `ErrorMessage` table, including:
  - Date.
  - Vacuum ID.
  - Operation type.
  - Error message.

---

## Data Model Overview

### Vacuum Attributes:
- **Name**: The name of the vacuum.  
- **Type**: The vacuum's type.  
- **Description**: Additional details about the vacuum.  
- **Status**: Current state (ON, OFF, DISCHARGING).  
- **Added By**: The user who added the vacuum.  
- **Active**: Indicates if the vacuum is active in the system.  

### Permissions:
Each action is associated with a specific permission:
- `can_search_vacuum`
- `can_start_vacuum`
- `can_stop_vacuum`
- `can_discharge_vacuum`
- `can_add_vacuum`
- `can_remove_vacuum`
