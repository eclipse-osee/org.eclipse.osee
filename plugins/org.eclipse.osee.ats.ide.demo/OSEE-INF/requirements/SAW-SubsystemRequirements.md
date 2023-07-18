# 1. Subsystem Requirements

## 1.1 Robot API

The robot Application Programming Interface with provide the software interface to all robots attached to the system.

## 1.2 Video processing

### 1.2.1 Video Capture

The system shall include hardware to capture video images from a number of sources, including stereo cameras and ultrasound.

### 1.2.2 3D surface reconstruction

The software shall include a method for 3D surface reconstruction from stereo video images (see 4.3).

### 1.2.3 Volumetric Images

In addition to video, the software shall accept inputs from other sources, such as prior volumetric images and models.

### 1.2.4 Video Files

The software should work with recorded data (e.g., video files).

### 1.2.5 Frame Synchronization

The software shall have a method for synchronizing the video frames to other data, such as robot feedback (see 4.7.7). One possibility is to utilize an image format that enables additional information to be stored as meta-data.

## 1.3 Other device interfaces

### 1.3.1 Input Devices

The software shall provide interfaces to various user input devices, such as foot pedals, switches, and buttons (including those on the daVinci master console).

### 1.3.2 Sensors

The software shall provide an interface to sensors that can measure forces and torques, including full 6-dof force/torque data.

### 1.3.3 Additional Device Integration

The architecture shall be extensible, allowing the integration of other devices not yet specified.

## 1.4 Calibration and registration

### 1.4.1 Calibration

The system shall support calibration of the following:

#### 1.4.1.1 Ultrasound probe

Position/orientation of the 2D image plane with respect to a 3D reference frame on the probe.

#### 1.4.1.2 Camera calibration (intrinsic)

The determination of camera parameters such as focal length, resolution, optical center, and lens distortion.

#### 1.4.1.3 Stereo camera calibration (extrinsic)

The transformation between two camera systems (or one camera moved between two positions).

#### 1.4.1.4 Robot kinematics

The transformation between a frame on the final link of the robot and a frame on the base of the robot.

#### 1.4.1.5 Tool tip

The transformation between a frame on the tip of a (rigid) tool and a frame defined elsewhere on the tool. This calibration may not require a full transformation; for example, a translation vector is sufficient if only the tip position is required.

### 1.4.2 Calibration Methods

The system shall support each calibration listed under “Calibration” by providing the following:

#### 1.4.2.1 Data Collection

The system may (optionally) provide functions for collecting calibration data. Otherwise, this can be provided by external software.

#### 1.4.2.2 Calibration Parameters

The system may (optionally) provide functions to compute the calibration parameters. Otherwise, this can be done by external software (e.g., Matlab program).

#### 1.4.2.3 Calibration Results

The system shall have a method for using the results of the calibration (e.g., by reading the results from a file).

### 1.4.3 Registration

#### 1.4.3.1 Coordinate Systems Registration

The system shall provide routines to register the following coordinate systems (i.e., find the transformation from one coordinate system to another coordinate system):

- stereo video image
- ultrasound image
- preoperative image (e.g., CT) or model (e.g., segmented volume)
- robot
- external measurement device (e.g., Optotrak)

Registration between any two of these coordinate systems shall either be computed directly or obtained by composition of two or more known registrations.

### 1.4.4 Coordinate Computation Methods

The system shall provide implementations of the following coordinate computation methods (for calibration and/or registration):

#### 1.4.4.1 Pivot Calibration

A “pivot calibration” method, where the transformation is computed from data obtained by pivoting a tool about a fixed position.

#### 1.4.4.2 Paired-point Rigid Registration

Paired-point rigid registration, using a method such as the one proposed in Reference 2.2.1 or in Reference 2.2.2 (enhanced in Reference 2.2.3).

##### 1.4.4.2.1 External Measurement

This method may be used to register the robot to the external measurement device.

##### 1.4.4.2.2 Preoperative Image

This method may be used to register the preoperative image or model to the robot or external measurement device.

#### 1.4.4.3 Iterative methods for registering sets of points/surfaces

An example is the Iterative Closest Point (ICP) algorithm proposed in Reference
This method may be used to register prior models to the anatomy without requiring fiducials .

#### 1.4.4.4 Matrix Equation

Solving the matrix equation AX = XB, where A and B are known and X is unknown. This method may be used for ultrasound calibration (see Reference 2.2.4).

#### 1.4.4.5 Deformable (non-rigid) registration

This method may be used for video overlay on organs.

## 1.5 Tool tracking

### 1.5.1 Tool Positions/Orientations Estimation

The software shall be able to estimate tool positions/orientations using a combination of stereo video images and joint encoder feedback.

### 1.5.2 Position Prediction

The software shall be able to predict the position and shape of tools in the 3D frames (i.e., to provide input to the video rendering).

## 1.6 User Interface (Visualization)

### 1.6.1 Supported Hardware

The system shall contain hardware to display the captured stereo images. At a minimum, the following hardware shall be supported:

- daVinci master console display
- Head-mounted display

### 1.6.2 Windowing System

The software shall include a windowing system to:

- Manage the display of multiple windows.
- Render visual objects, including menus, buttons, toolbars, and images (see 4.6.3), in the 3D viewing space.

### 1.6.3 Image Rendering

The system shall be capable of rendering the following images:

- 2D images
- 3D volume models
- 3D surface models
- 2D image projected onto a 3D plane (e.g., laparoscopic ultrasound)

### 1.6.4 Image Fusion

The system shall be capable of performing image fusion, i.e., the ability to combine multiple images with different blending/overlay parameters.

### 1.6.5 Object Manipulation

The system shall support the manipulation of virtual objects in the surgeon’s field of view, where the virtual objects may be 2D or 3D images or models, or widgets such as “in volume” menus and virtual push-buttons, and similar functions. The manipulation functions shall be designed to accept position/orientation inputs from a generic user input device. One embodiment shall include the daVinci master telemanipulators and associated surgeon console controls. The manipulation functions shall include controls for manipulating the virtual objects (e.g., repositioning, rotating, scaling) and for turning them on and off.

## 1.7 Telesurgery application framework

The application framework shall integrate all functions listed above.

### 1.7.1 Event Loop

The application framework shall contain an event loop to handle events from the subsystem components.

### 1.7.2 Video Data Pipeline

The application framework shall include a real-time data pipeline that can be used by video processing subsystem (4.2).

### 1.7.3 CISST

The application framework shall include the CISST Interactive Research Environment (IRE), which is a Python-based shell for interactive development.

### 1.7.4 Plug-in Modules

The application framework shall allow users to dynamically load “plug-in” modules for research purposes.

### 1.7.5 Modular Architecture

The application framework shall have a modular architecture, allowing it to be implemented on different physical architectures (i.e., different boxes), within the performance (bandwidth and latency) limitations of the interconnections. Supported physical architectures shall include any of the following:

- Ultrasound acquisition on a separate computer.
- Robot control on a separate computer (e.g., daVinci embedded controller or external JHU controller).
- Collaborative robots controlled by different computers.
- All functions specified in this requirement on a single PC.

### 1.7.6 Data Logging

The application framework shall include a flexible data logging mechanism to allow the recording of relevant state information, including video. This can, for example, be used for research in gesture recognition.

### 1.7.7 Time Synchronization

The data logging shall support time synchronization, either by ensuring that all data are captured at the same time or by associating a system-wide “timestamp” with each data item.

### 1.7.8 System Recovery

The system shall provide a method to save an occasional “snapshot” of the state for recovery from system restart (e.g., due to power failure, computer crash, etc.).

## 1.8 Volume viewer

### 1.8.1 Functions

The volume viewer shall be implemented using the functions specified in Section 4.6.5.

### 1.8.2 Volume Data Sets

The volume viewer shall provide functions for selecting and loading volume data sets from a menu of choices.

### 1.8.3 Scaling

The volume viewer shall provide functions for scaling the data set from voxels to physical coordinates and placing it at a specified position within the stereoscopic visualization coordinate system (i.e., in camera coordinates).

### 1.8.4 Fused Visualizations

The volume viewer shall provide functions for turning visualization on and off and providing “fused” visualizations by video blending (see Section 4.6.4).

### 1.8.5 Haptic Interaction

The volume viewer shall provide functions to enable haptic interaction with volumetric data (Phase 2)

### 1.8.6 Motion Compensation

The volume viewer shall update the visualization to compensate for camera motion, so that the volumetric data set appears to be fixed to the tissue (Phase 2).

### 1.8.7 Third-party Integration

The interface between the input device(s) and volume viewer shall be as generic as possible to facilitate integration of third-party volume viewer software.
