use std::fmt::Debug;

/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
use applicability::applic_tag::ApplicabilityTag;
use applicability_document_schema::StringOrByteArray;
use applicability_lexer_applicability_structure_code_block::CodeBlock;
use applicability_lexer_applicability_structure_multi_line::MultiLine;
use applicability_lexer_applicability_structure_single_line_non_terminated::SingleLineNonTerminated;
use applicability_lexer_applicability_structure_single_line_terminated::SingleLineTerminated;
use applicability_lexer_base::applicability_structure::LexerToken;
use applicability_lexer_chunker::chunk;
use applicability_lexer_document_structure::document_structure_parser::IdentifyComments;
use applicability_lexer_multi_stage_lexer::lexer::tokenize_comments;
use applicability_parser_errors::ApplicabilityParserError;
use applicability_tokens_to_ast::{transform_tokens, tree::ApplicabilityExprKind};
use nom::{AsBytes, AsChar, Compare, FindSubstring, Input, Offset};
use nom_locate::LocatedSpan;
use rayon::iter::{IntoParallelIterator, ParallelIterator};

type ParseApplicabilityInput<I> = LocatedSpan<I, ((usize, u32, usize), (usize, u32, usize))>;
type ParseApplicabilityResult<I> = Vec<ApplicabilityExprKind<I>>;
pub fn parse_applicability<'a, 'b, I, T>(
    input: ParseApplicabilityInput<I>,
    doc: &T,
) -> Result<ParseApplicabilityResult<I>, ApplicabilityParserError>
where
    I: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default
        + Debug
        + 'a,
    <I as Input>::Item: AsChar,
    StringOrByteArray<'b>: From<I>,
    'a: 'b,
    ApplicabilityTag<I, String>: From<I>,
    T: IdentifyComments
        + SingleLineTerminated
        + SingleLineNonTerminated
        + MultiLine
        + CodeBlock
        + Sync,
{
    let tokens = match tokenize_comments(doc, input) {
        Ok(t) => Ok(t
            .1
            .into_iter()
            .map(Into::<LexerToken<I>>::into)
            .collect::<Vec<LexerToken<I>>>()),
        Err(e) => Err(e),
    };
    let chunks = match tokens {
        Ok(t) => Ok(chunk(t)),
        Result::Err(e) => Err(e),
    };
    match chunks {
        Ok(c) => match c
            .into_par_iter()
            .map(|chunk| transform_tokens(chunk))
            .collect::<Result<_, _>>()
        {
            Ok(x) => Ok(x),
            Result::Err(e) => Err(e.into()),
        },
        Result::Err(e) => Err(e.into()),
    }
}

#[cfg(test)]
mod tests {
    use applicability::applic_tag::ApplicabilityTag;
    use applicability_lexer_config_markdown::ApplicabilityMarkdownLexerConfig;
    use applicability_parser_errors::AstTransformError;
    use applicability_parser_types::applic_tokens::{
        ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedNotOrTag, ApplicabilityNoTag,
        ApplicabilityOrTag,
    };
    use applicability_tokens_to_ast::{
        tree::{
            ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition,
            ApplicabilityExprKind, ApplicabilityExprSubstitution, ApplicabilityExprTag,
            ApplicabilityKind, Text,
        },
        updatable::UpdatableValue,
    };
    use nom_locate::LocatedSpan;

    use crate::parse_applicability;
    use pretty_assertions::assert_eq;

    #[test]
    fn saw_system_req() {
        let sample_markdown_input = r#"# 1. Objective

This document defines the requirements for the Surgical Assistant Workstation for Teleoperated Surgical Robots (SAWTSR) being developed by Johns Hopkins University and Intuitive Surgical. This document provides requirements for the workstation system and does not include application requirements. It is expected that applications developed on this workstation would define their own requirements (i.e., in a separate document).

# 2. References

## 2.1 Project-Specific

- “Development of a Surgical Assistant Workstation for Teleoperated Surgical Robots,” proposal for NSF ERC Supplement, July 2006.
- “Intuitive Surgical daVinci API v5.0 Reference Manual”, generated July 14, 2006.
- J. Leven, D. Burschka, R. Kumar, G. Zhang, S. J. Blumenkranz, X. Dai, M. Awad, G. Hager, M. Marohn, M. Choti, C. Hasser and R. H. Taylor “DaVinci Canvas: A Telerobotic Surgical System with Integrated, Robot-Assisted, Laparoscopic Ultrasound Capability,” in MICCAI, vol. LNCS 3749, J. Duncan and G. Gerig, Eds. Palm Springs, CA: Springer-Verlag, 2005, pp. 811-818.
- J. Leven, “A Telerobotic Surgical Systems with Integrated Robot-Assisted Laparoscopic Ultrasound Capability”, MS Thesis, Computer Science, Johns Hopkins University, Baltimore, 2005.

## 2.2 Calibration and Registration Techniques

- B.K.P. Horn, “Closed-form solution of absolute orientation using unit quaternions,” J. Opt. Soc. Amer. A, Vol. 4, No. 4, pp. 629–642, Apr. 1987.
- K.S. Arun, T.S. Huang, S.D. Blostein, “Least-Squares Fitting of Two 3D Point Sets”, IEEE PAMI, Vol. 9, No. 4, pp. 698-700, Sept. 1987.
- S. Umeyama, “Least-Squares Estimation of Transformation Parameters Between Two Point Patterns”, IEEE PAMI, Vol. 13, No. 4, pp. 376-380, Apr. 1991.
- Boctor, A. Viswanathan, M. Choti, R. Taylor, G. Fichtinger, G. Hager, “A Novel Closed Form Solution for Ultrasound Calibration,” IEEE Intl. Symp. Bio. Imag. (ISBI), Arlington, VA, pp 527-530, Apr. 2004.
- P. J. Besl and N. D. McKay, "A Method for Registration of 3-D Shapes," IEEE Transactions on Pattern Analysis and Machine Intelligence, vol. 14, pp. 239256, Feb. 1992.

## 2.3 Virtual Fixtures and Constrained Optimization

- Kapoor, M. Li, R.H. Taylor, “Constrained Control for Surgical Assistant Robots”, Proc. IEEE Intl. Conf. on Robotics and Automation, Orlando, FL, May 2006, pp 231-236.
- M. Li, A. Kapoor and R. H. Taylor “A Constrained Optimization Approach to Virtual Fixtures,” in IROS. Edmonton, Alberta, Canada, 2005.
- M. Li and R. H. Taylor, “Performance of Teleoperated and cooperatively controlled surgical robots with automatically generated spatial virtual fixtures.,” in IEEE International Conference on Robotics and Automation. Barcelona, Spain, 2005.
- M. Li, “Intelligent Robotic Surgical Assistance for Sinus Surgery”, Ph.D. Thesis, Computer Science, The Johns Hopkins University, Baltimore, Maryland, 2005.
- M. Li and R. H. Taylor, “Spatial Motion Constraints in Medical Robots Using Virtual Fixtures Generated by Anatomy,” in IEEE Conf. on Robotics and Automation. New Orleans, 2004, pp. 1270-1275.
  - See <artifact-link>1633658933</artifact-link>.

# 3. Robot System Overview

The goal is to create a unified assistive environment for surgery that integrates robotic devices; fused information environments combining preoperative images & models, intraoperative images & other sensors; surgical task modeling; and human-machine cooperative manipulation, as shown in Figure 1 (from Reference 2.1.1).

<image-link>6829343659904028894</image-link>

Figure 1: Overview of SAWTSR Architecture
In this document, the system requirements are categorized by the major subsystem:

## 3.1 Robot API Subsystem

This subsystem corresponds to the interface to the robot or robots; although the design shall be influenced by the existing daVinci research API, it shall be usable for other robot systems.

## 3.2 Video processing Subsystem

This subsystem provides the processing of 2D (e.g., ultrasound) and 3D (e.g., stereo video) images, using image pipelines.

## 3.3 Other device interfaces Subsystem

This subsystem provides interfaces to devices other than robots and video systems, including force sensors, foot pedals, tissue oxygenation sensors, etc.

## 3.4 Calibration and registration Subsystem

This subsystem provides tools for calibrating devices or (at a minimum) for reading the calibration results produced by an external system (e.g., Matlab programs). It also provides methods for computing coordinate transformations (e.g., registration).

## 3.5 Tool tracking Subsystem

This subsystem provides the capability for tracking the positions of tools using some combination of sensor feedback (e.g., joint encoder positions and stereo video images).

## 3.6 User Interface (Visualization) Subsystem

This subsystem provides the 2D and 3D graphical displays and accepts control information from input devices (including the master manipulators in a telesurgical system).

## 3.7 Telesurgery application framework

This corresponds to a working “skeleton” application, which the researcher can customize. System-level requirements are listed in this category.

<image-link>8446203177483923452</image-link>

Figure 2: Illustrative data flow

## 3.8 Volume Viewer

This is an application-level “widget” that allows the user to view and manipulate medical images, using the daVinci master manipulators as input devices.
Figure 2 shows an illustrative data flow diagram, focusing on the robot API and the pipeline for the video processing and visualization. This figure also shows the tool tracking and volume viewer subsystems. Although not specifically shown, calibration and registration functions are required. Note that other data flow configurations are possible, depending on the application requirements.

# 4. Performance Requirements

The system shall have at least one periodic loop (“heartbeat”) that interacts with the hardware devices and/or proprietary device interface software. Many of the following performance requirements depend on the frequency of this periodic loop.

## 4.1 Robot API

Note: The following performance parameters are highly dependent on the particular robot that is used. The numbers cited below apply to the daVinci robot systems, but it is expected that other robot systems will meet or exceed these minimum requirements.

### 4.1.1 Read-only Minimum Rate

The minimum update rate for receiving state information (e.g., robot positions) from a read-only robot shall be 50 Hz (20 msec)

### 4.1.2 Read-Write Minimum Rate

The minimum update rate for receiving state information (e.g., robot positions) from a read-write robot shall be 30 Hz.

### 4.1.3 Commanding Minimum Rate

The minimum update rate for commanding state changes (e.g., providing position goals) shall be 30 Hz, subject to physical constraints (e.g., robot must be able to reach target positions within update cycle).

### 4.1.4 Detection Latency

The maximum latency between detection of a physical state change and availability of this information from the robot API shall be 100 msec.

### 4.1.5 Commanded State Latency

The maximum latency between a commanded state change and the corresponding hardware output shall be 100 msec.

## 4.2 Video processing

### 4.2.1 Video Capture Frame Rate

The video capture frame rate shall be 30 frames per second.

### 4.2.2 Video Capture Latency

The latency due to video capture shall not exceed 2 time frames (depends on video capture hardware).

### 4.2.3 Overlayed Video Frame Rate

The processed and overlayed video frame rate shall be at least 10 frames per second.

### 4.2.4 Video Processing Latency

The latency due to video processing shall not exceed 1 time frame (e.g., 100 msec at 10 frames per second).

### 4.2.5 Stereo Reconstruction Resolution

The stereo reconstruction resolution is a function of the baseline width (distance between cameras), depth (distance from the cameras), camera resolution, and focal length. For a baseline of 5 mm (worst case for daVinci endoscope) and camera resolution of 640 x 480, the following resolutions are obtained (results in mm):

|               |         | **Focal length, pixels** |         |          |
| :------------ | :-----: | :----------------------: | :-----: | :------: |
| **Depth, mm** | **700** |         **800**          | **900** | **1000** |
| **50**        |  0.70   |           0.62           |  0.55   |   0.50   |
| **100**       |  2.78   |           2.44           |  2.17   |   1.96   |
| **150**       |  6.16   |           5.42           |  4.84   |   4.37   |
| **200**       |  10.81  |           9.52           |  8.51   |   7.69   |
| **250**       |  16.67  |          14.71           |  13.16  |  11.90   |

### 4.2.6 Registration Error Limit

The registration error between 3D anatomic models and the live (video) image shall not exceed 1.5 times (150% of) the stereo reconstruction resolution, at a specified depth and focal length (see 5.2.5), not including errors due to organ deformation.

### 4.2.7 Initial Registration Limit

The initial registration shall require no more than 1 second of computation time.

### 4.2.8 Visualization Latency

The latency due to the visualization shall not exceed 10 msec (depends on video output hardware). See <artifact-link>1970889096</artifact-link>.

## 4.3 Other device interfaces

### 4.3.1 Minimum Update Rate

The minimum update rate for receiving information from other devices (e.g., force sensor, tracker, etc.) shall be 50 Hz (20 msec).

### 4.3.2 Force Data Latency

The latency of the force data (e.g., time between physical application of force and software reception of force measurement) shall be no more than 40 msec.

### 4.3.3 Tracker Data Latency

The latency of the tracker data (e.g., time between physical motion and software reception of new position) shall be no more than 100 msec.

## 4.4 Calibration and registration

Note: Calibration and registration performance requirements shall be specified in the application requirements document, rather than in this system (workstation) requirements document.

## 4.5 Tool Tracking

Note: The following specifications are for the daVinci stereoscopic endoscopes and the Intuitive tool tracking software implementation.

### 4.5.1 Tool Tracking Update Rate

The update rate of Tool Tracking shall be no more than 200 ms.

### 4.5.2 Tool Tracking Latency

The latency of Tool Tracking shall be no more than the update rate (the time to process 1 frame).

### 4.5.3 Position Accuracy

The position of an instrument shall be determined with an average accuracy of at least 4 mm, at a distance of 75-80 mm.

### 4.5.4 Output Accuracy

The accuracy of outputs from external tools, shall be measured with a tolerance level not exceeding ±1% of the specified performance metrics as defined by industry standards or project requirements.

``Feature[ROBOT_SPEAKER=SPKR_A]`` The speaker output shall reproduce audio frequencies within ±1% of the specified frequency response range (e.g., 20 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±.5 dB of the reference level at 1 meter in a controlled acoustic environment. ``End Feature``
``Feature[ROBOT_SPEAKER=SPKR_B]`` The speaker output shall reproduce audio frequencies within ±1% of the specified frequency response range (e.g., 45 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±1 dB of the reference level at 1 meter in a controlled acoustic environment. ``End Feature``

``ConfigurationGroup[abGroup]`` The speaker shall have a water-resistant rating of IPX4. ``End ConfigurationGroup``
``ConfigurationGroup[cdGroup]`` The speaker shall have a water-resistant rating of IPX5. ``End ConfigurationGroup``

``Feature[ROBOT_ARM_LIGHT=Included]`` The light shall support variable brightness levels from 10% to 100% of its maximum rated output, with luminous intensity accuracy within ±1% of the commanded lumen value across the range. The light shall also support multiple color profiles with correlated color temperature (CCT) values ranging from 2700K to 6500K, and shall maintain CCT accuracy within ±1% of the selected profile under standard operating conditions. ``End Feature``

## 4.6 User Interface PR (Visualization)

## 4.7 Telesurgery application framework

### 4.7.1 Time Synchronization

The time synchronization between different components shall be defined by the minimum (slowest) update rate of the components (e.g., if the slowest component updates every 100 msec, the time synchronization shall be within 100 msec).

### 4.7.2 System Heartbeat

The system heartbeat shall be 20 msec or less.

## 4.8 Volume viewer

# 5. Safety Requirements

## 5.1 Robot API

### 5.1.1 Power Disable

The read-write Robot API shall include a software command to allow application programs to disable power to the robot motors.

### 5.1.2 Emergency Disengagement

The system shall provide a method for disengaging the research interface from any clinical robot capable of operating in a stand-alone manner (e.g., the daVinci) for emergency responses.

## 5.2 Video processing

## 5.3 Other device interfaces

### 5.3.1 Emergency Stop Switch

The system shall include an “emergency stop” switch that disables power to the robot motors and any other potentially hazardous device. An appropriate signal shall be sent when this occurs. See <artifact-link>1734668983</artifact-link>.

## 5.4 Calibration and registration

### 5.4.1 Computation Methods

The computation methods shall indicate the residual error, so that users can determine how much confidence to place in the result.

## 5.5 Tool tracking

## 5.6 User Interface (Visualization)

### 5.6.1 Unmodified Visualization

The system shall provide a method for disengaging the research visualization output, so that a clinician can revert to the visualization provided by an unmodified clinical robot (e.g., a clinical daVinci).

## 5.7 Telesurgery application framework

### 5.7.1 Periodic Safety Check

The application framework shall periodically check all safety-critical subsystems (e.g., by verifying communication integrity) and initiate a safety response (e.g., using function 6.1.1) if a failure is detected.

## 5.8 Volume viewer

# 6. Design Constraints

## 6.1 Operating System

The system shall be designed to operate on Red Hat Enterprise Linux WS 4. It is desirable for it to work with any type of Linux, with future extension to a real-time Linux such as RTAI.

## 6.2 Programming Language

The software shall be written in C/C++.

<image-link>1646203177483523742</image-link>

Figure 3: C/C++ Language

## 6.3 Software Libraries

### 6.3.1 CISST Libraries

The software shall use the CISST libraries.

### 6.3.2 VTK

The software shall use the Visualization Toolkit (VTK) for visualization of images. The software may optionally use other toolkits that build on, or extend, VTK.

### 6.3.3 Intuitive Surgical

The tool tracking module shall be based on existing code from Intuitive Surgical.

### 6.3.4 daVinci research API

The daVinci research API shall be jointly evaluated by ISI and JHU to determine whether to add a requirement to port it to use the CISST operating system abstraction and real-time support libraries (for the thread that manages the data stream).
"#;
        let doc_config: ApplicabilityMarkdownLexerConfig =
            ApplicabilityMarkdownLexerConfig::default();
        let results = parse_applicability(
            LocatedSpan::new_extra(
                sample_markdown_input,
                ((0usize, 0, 0usize), (0usize, 0, 0usize)),
            ),
            &doc_config,
        );
        let first_block = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::Text(Text {
                text: r#"# 1. Objective

This document defines the requirements for the Surgical Assistant Workstation for Teleoperated Surgical Robots (SAWTSR) being developed by Johns Hopkins University and Intuitive Surgical. This document provides requirements for the workstation system and does not include application requirements. It is expected that applications developed on this workstation would define their own requirements (i.e., in a separate document).

# 2. References

## 2.1 Project-Specific

- “Development of a Surgical Assistant Workstation for Teleoperated Surgical Robots,” proposal for NSF ERC Supplement, July 2006.
- “Intuitive Surgical daVinci API v5.0 Reference Manual”, generated July 14, 2006.
- J. Leven, D. Burschka, R. Kumar, G. Zhang, S. J. Blumenkranz, X. Dai, M. Awad, G. Hager, M. Marohn, M. Choti, C. Hasser and R. H. Taylor “DaVinci Canvas: A Telerobotic Surgical System with Integrated, Robot-Assisted, Laparoscopic Ultrasound Capability,” in MICCAI, vol. LNCS 3749, J. Duncan and G. Gerig, Eds. Palm Springs, CA: Springer-Verlag, 2005, pp. 811-818.
- J. Leven, “A Telerobotic Surgical Systems with Integrated Robot-Assisted Laparoscopic Ultrasound Capability”, MS Thesis, Computer Science, Johns Hopkins University, Baltimore, 2005.

## 2.2 Calibration and Registration Techniques

- B.K.P. Horn, “Closed-form solution of absolute orientation using unit quaternions,” J. Opt. Soc. Amer. A, Vol. 4, No. 4, pp. 629–642, Apr. 1987.
- K.S. Arun, T.S. Huang, S.D. Blostein, “Least-Squares Fitting of Two 3D Point Sets”, IEEE PAMI, Vol. 9, No. 4, pp. 698-700, Sept. 1987.
- S. Umeyama, “Least-Squares Estimation of Transformation Parameters Between Two Point Patterns”, IEEE PAMI, Vol. 13, No. 4, pp. 376-380, Apr. 1991.
- Boctor, A. Viswanathan, M. Choti, R. Taylor, G. Fichtinger, G. Hager, “A Novel Closed Form Solution for Ultrasound Calibration,” IEEE Intl. Symp. Bio. Imag. (ISBI), Arlington, VA, pp 527-530, Apr. 2004.
- P. J. Besl and N. D. McKay, "A Method for Registration of 3-D Shapes," IEEE Transactions on Pattern Analysis and Machine Intelligence, vol. 14, pp. 239256, Feb. 1992.

## 2.3 Virtual Fixtures and Constrained Optimization

- Kapoor, M. Li, R.H. Taylor, “Constrained Control for Surgical Assistant Robots”, Proc. IEEE Intl. Conf. on Robotics and Automation, Orlando, FL, May 2006, pp 231-236.
- M. Li, A. Kapoor and R. H. Taylor “A Constrained Optimization Approach to Virtual Fixtures,” in IROS. Edmonton, Alberta, Canada, 2005.
- M. Li and R. H. Taylor, “Performance of Teleoperated and cooperatively controlled surgical robots with automatically generated spatial virtual fixtures.,” in IEEE International Conference on Robotics and Automation. Barcelona, Spain, 2005.
- M. Li, “Intelligent Robotic Surgical Assistance for Sinus Surgery”, Ph.D. Thesis, Computer Science, The Johns Hopkins University, Baltimore, Maryland, 2005.
- M. Li and R. H. Taylor, “Spatial Motion Constraints in Medical Robots Using Virtual Fixtures Generated by Anatomy,” in IEEE Conf. on Robotics and Automation. New Orleans, 2004, pp. 1270-1275.
  - See <artifact-link>1633658933</artifact-link>.

# 3. Robot System Overview

The goal is to create a unified assistive environment for surgery that integrates robotic devices; fused information environments combining preoperative images & models, intraoperative images & other sensors; surgical task modeling; and human-machine cooperative manipulation, as shown in Figure 1 (from Reference 2.1.1).

<image-link>6829343659904028894</image-link>

Figure 1: Overview of SAWTSR Architecture
In this document, the system requirements are categorized by the major subsystem:

## 3.1 Robot API Subsystem

This subsystem corresponds to the interface to the robot or robots; although the design shall be influenced by the existing daVinci research API, it shall be usable for other robot systems.

## 3.2 Video processing Subsystem

This subsystem provides the processing of 2D (e.g., ultrasound) and 3D (e.g., stereo video) images, using image pipelines.

## 3.3 Other device interfaces Subsystem

This subsystem provides interfaces to devices other than robots and video systems, including force sensors, foot pedals, tissue oxygenation sensors, etc.

## 3.4 Calibration and registration Subsystem

This subsystem provides tools for calibrating devices or (at a minimum) for reading the calibration results produced by an external system (e.g., Matlab programs). It also provides methods for computing coordinate transformations (e.g., registration).

## 3.5 Tool tracking Subsystem

This subsystem provides the capability for tracking the positions of tools using some combination of sensor feedback (e.g., joint encoder positions and stereo video images).

## 3.6 User Interface (Visualization) Subsystem

This subsystem provides the 2D and 3D graphical displays and accepts control information from input devices (including the master manipulators in a telesurgical system).

## 3.7 Telesurgery application framework

This corresponds to a working “skeleton” application, which the researcher can customize. System-level requirements are listed in this category.

<image-link>8446203177483923452</image-link>

Figure 2: Illustrative data flow

## 3.8 Volume Viewer

This is an application-level “widget” that allows the user to view and manipulate medical images, using the daVinci master manipulators as input devices.
Figure 2 shows an illustrative data flow diagram, focusing on the robot API and the pipeline for the video processing and visualization. This figure also shows the tool tracking and volume viewer subsystems. Although not specifically shown, calibration and registration functions are required. Note that other data flow configurations are possible, depending on the application requirements.

# 4. Performance Requirements

The system shall have at least one periodic loop (“heartbeat”) that interacts with the hardware devices and/or proprietary device interface software. Many of the following performance requirements depend on the frequency of this periodic loop.

## 4.1 Robot API

Note: The following performance parameters are highly dependent on the particular robot that is used. The numbers cited below apply to the daVinci robot systems, but it is expected that other robot systems will meet or exceed these minimum requirements.

### 4.1.1 Read-only Minimum Rate

The minimum update rate for receiving state information (e.g., robot positions) from a read-only robot shall be 50 Hz (20 msec)

### 4.1.2 Read-Write Minimum Rate

The minimum update rate for receiving state information (e.g., robot positions) from a read-write robot shall be 30 Hz.

### 4.1.3 Commanding Minimum Rate

The minimum update rate for commanding state changes (e.g., providing position goals) shall be 30 Hz, subject to physical constraints (e.g., robot must be able to reach target positions within update cycle).

### 4.1.4 Detection Latency

The maximum latency between detection of a physical state change and availability of this information from the robot API shall be 100 msec.

### 4.1.5 Commanded State Latency

The maximum latency between a commanded state change and the corresponding hardware output shall be 100 msec.

## 4.2 Video processing

### 4.2.1 Video Capture Frame Rate

The video capture frame rate shall be 30 frames per second.

### 4.2.2 Video Capture Latency

The latency due to video capture shall not exceed 2 time frames (depends on video capture hardware).

### 4.2.3 Overlayed Video Frame Rate

The processed and overlayed video frame rate shall be at least 10 frames per second.

### 4.2.4 Video Processing Latency

The latency due to video processing shall not exceed 1 time frame (e.g., 100 msec at 10 frames per second).

### 4.2.5 Stereo Reconstruction Resolution

The stereo reconstruction resolution is a function of the baseline width (distance between cameras), depth (distance from the cameras), camera resolution, and focal length. For a baseline of 5 mm (worst case for daVinci endoscope) and camera resolution of 640 x 480, the following resolutions are obtained (results in mm):

|               |         | **Focal length, pixels** |         |          |
| :------------ | :-----: | :----------------------: | :-----: | :------: |
| **Depth, mm** | **700** |         **800**          | **900** | **1000** |
| **50**        |  0.70   |           0.62           |  0.55   |   0.50   |
| **100**       |  2.78   |           2.44           |  2.17   |   1.96   |
| **150**       |  6.16   |           5.42           |  4.84   |   4.37   |
| **200**       |  10.81  |           9.52           |  8.51   |   7.69   |
| **250**       |  16.67  |          14.71           |  13.16  |  11.90   |

### 4.2.6 Registration Error Limit

The registration error between 3D anatomic models and the live (video) image shall not exceed 1.5 times (150% of) the stereo reconstruction resolution, at a specified depth and focal length (see 5.2.5), not including errors due to organ deformation.

### 4.2.7 Initial Registration Limit

The initial registration shall require no more than 1 second of computation time.

### 4.2.8 Visualization Latency

The latency due to the visualization shall not exceed 10 msec (depends on video output hardware). See <artifact-link>1970889096</artifact-link>.

## 4.3 Other device interfaces

### 4.3.1 Minimum Update Rate

The minimum update rate for receiving information from other devices (e.g., force sensor, tracker, etc.) shall be 50 Hz (20 msec).

### 4.3.2 Force Data Latency

The latency of the force data (e.g., time between physical application of force and software reception of force measurement) shall be no more than 40 msec.

### 4.3.3 Tracker Data Latency

The latency of the tracker data (e.g., time between physical motion and software reception of new position) shall be no more than 100 msec.

## 4.4 Calibration and registration

Note: Calibration and registration performance requirements shall be specified in the application requirements document, rather than in this system (workstation) requirements document.

## 4.5 Tool Tracking

Note: The following specifications are for the daVinci stereoscopic endoscopes and the Intuitive tool tracking software implementation.

### 4.5.1 Tool Tracking Update Rate

The update rate of Tool Tracking shall be no more than 200 ms.

### 4.5.2 Tool Tracking Latency

The latency of Tool Tracking shall be no more than the update rate (the time to process 1 frame).

### 4.5.3 Position Accuracy

The position of an instrument shall be determined with an average accuracy of at least 4 mm, at a distance of 75-80 mm.

### 4.5.4 Output Accuracy

The accuracy of outputs from external tools, shall be measured with a tolerance level not exceeding ±1% of the specified performance metrics as defined by industry standards or project requirements.

"#,
                start_position: UpdatableValue {
                    previous_value: (0, 1, 1),
                    current_value: (0, 1, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (10908, 186, 1),
                    current_value: (10908, 186, 1),
                },
            })],
        });
        let first_feature = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
                    contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "ROBOT_SPEAKER",
                                value: "SPKR_A".to_string(),
                            },
                            None,
                        ))],
                        kind: ApplicabilityKind::Feature,
                        contents: vec![ApplicabilityExprKind::Text(Text {
                            text: r#" The speaker output shall reproduce audio frequencies within ±1% of the specified frequency response range (e.g., 20 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±.5 dB of the reference level at 1 meter in a controlled acoustic environment. "#,
                            start_position: UpdatableValue {
                                previous_value: (10941, 186, 34),
                                current_value: (10941, 186, 34),
                            },
                            end_position: UpdatableValue {
                                previous_value: (11205, 186, 298),
                                current_value: (11205, 186, 298),
                            },
                        })],
                        start_position: UpdatableValue {
                            previous_value: (10908, 186, 1),
                            current_value: (10908, 186, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (10917, 186, 10),
                            current_value: (11221, 187, 1),
                        },
                    })],
                    start_position: UpdatableValue {
                        previous_value: (10908, 186, 1),
                        current_value: (10908, 186, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (11221, 187, 1),
                    },
                },
            )],
        });
        let second_feature = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
                    contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "ROBOT_SPEAKER",
                                value: "SPKR_B".to_string(),
                            },
                            None,
                        ))],
                        kind: ApplicabilityKind::Feature,
                        contents: vec![ApplicabilityExprKind::Text(Text {
                            text: r#" The speaker output shall reproduce audio frequencies within ±1% of the specified frequency response range (e.g., 45 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±1 dB of the reference level at 1 meter in a controlled acoustic environment. "#,
                            start_position: UpdatableValue {
                                previous_value: (11254, 187, 34),
                                current_value: (11254, 187, 34),
                            },
                            end_position: UpdatableValue {
                                previous_value: (11517, 187, 297),
                                current_value: (11517, 187, 297),
                            },
                        })],
                        start_position: UpdatableValue {
                            previous_value: (11221, 187, 1),
                            current_value: (11221, 187, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (11230, 187, 10),
                            current_value: (11533, 188, 1),
                        },
                    })],
                    start_position: UpdatableValue {
                        previous_value: (11221, 187, 1),
                        current_value: (11221, 187, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (11533, 188, 1),
                    },
                },
            )],
        });
        let first_empty_space = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::Text(Text {
                text: "\n",
                start_position: UpdatableValue {
                    previous_value: (11533, 188, 1),
                    current_value: (11533, 188, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (11534, 189, 1),
                    current_value: (11534, 189, 1),
                },
            })],
        });
        let first_config_group = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
                    contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "abGroup",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        kind: ApplicabilityKind::ConfigurationGroup,
                        contents: vec![ApplicabilityExprKind::Text(Text {
                            text: " The speaker shall have a water-resistant rating of IPX4. ",
                            start_position: UpdatableValue {
                                previous_value: (11565, 189, 32),
                                current_value: (11565, 189, 32),
                            },
                            end_position: UpdatableValue {
                                previous_value: (11623, 189, 90),
                                current_value: (11623, 189, 90),
                            },
                        })],
                        start_position: UpdatableValue {
                            previous_value: (11534, 189, 1),
                            current_value: (11534, 189, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (11554, 189, 21),
                            current_value: (11650, 190, 1),
                        },
                    })],
                    start_position: UpdatableValue {
                        previous_value: (11534, 189, 1),
                        current_value: (11534, 189, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (11650, 190, 1),
                    },
                },
            )],
        });
        let second_config_group = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
                    contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "cdGroup",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        kind: ApplicabilityKind::ConfigurationGroup,
                        contents: vec![ApplicabilityExprKind::Text(Text {
                            text: " The speaker shall have a water-resistant rating of IPX5. ",
                            start_position: UpdatableValue {
                                previous_value: (11681, 190, 32),
                                current_value: (11681, 190, 32),
                            },
                            end_position: UpdatableValue {
                                previous_value: (11739, 190, 90),
                                current_value: (11739, 190, 90),
                            },
                        })],
                        start_position: UpdatableValue {
                            previous_value: (11650, 190, 1),
                            current_value: (11650, 190, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (11670, 190, 21),
                            current_value: (11766, 191, 1),
                        },
                    })],
                    start_position: UpdatableValue {
                        previous_value: (11650, 190, 1),
                        current_value: (11650, 190, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (11766, 191, 1),
                    },
                },
            )],
        });
        let second_empty_space = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::Text(Text {
                text: "\n",
                start_position: UpdatableValue {
                    previous_value: (11766, 191, 1),
                    current_value: (11766, 191, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (11767, 192, 1),
                    current_value: (11767, 192, 1),
                },
            })],
        });
        let third_feature = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
                    contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "ROBOT_ARM_LIGHT",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        kind: ApplicabilityKind::Feature,
                        contents: vec![ApplicabilityExprKind::Text(Text {
                            text: " The light shall support variable brightness levels from 10% to 100% of its maximum rated output, with luminous intensity accuracy within ±1% of the commanded lumen value across the range. The light shall also support multiple color profiles with correlated color temperature (CCT) values ranging from 2700K to 6500K, and shall maintain CCT accuracy within ±1% of the selected profile under standard operating conditions. ",
                            start_position: UpdatableValue {
                                previous_value: (11804, 192, 38),
                                current_value: (11804, 192, 38),
                            },
                            end_position: UpdatableValue {
                                previous_value: (12228, 192, 462),
                                current_value: (12228, 192, 462),
                            },
                        })],
                        start_position: UpdatableValue {
                            previous_value: (11767, 192, 1),
                            current_value: (11767, 192, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (11776, 192, 10),
                            current_value: (12244, 193, 1),
                        },
                    })],
                    start_position: UpdatableValue {
                        previous_value: (11767, 192, 1),
                        current_value: (11767, 192, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (12244, 193, 1),
                    },
                },
            )],
        });
        let final_text = ApplicabilityExprKind::None(ApplicabilityExprContainer {
            contents: vec![ApplicabilityExprKind::Text(Text {
                text: "\n## 4.6 User Interface PR (Visualization)\n\n## 4.7 Telesurgery application framework\n\n### 4.7.1 Time Synchronization\n\nThe time synchronization between different components shall be defined by the minimum (slowest) update rate of the components (e.g., if the slowest component updates every 100 msec, the time synchronization shall be within 100 msec).\n\n### 4.7.2 System Heartbeat\n\nThe system heartbeat shall be 20 msec or less.\n\n## 4.8 Volume viewer\n\n# 5. Safety Requirements\n\n## 5.1 Robot API\n\n### 5.1.1 Power Disable\n\nThe read-write Robot API shall include a software command to allow application programs to disable power to the robot motors.\n\n### 5.1.2 Emergency Disengagement\n\nThe system shall provide a method for disengaging the research interface from any clinical robot capable of operating in a stand-alone manner (e.g., the daVinci) for emergency responses.\n\n## 5.2 Video processing\n\n## 5.3 Other device interfaces\n\n### 5.3.1 Emergency Stop Switch\n\nThe system shall include an “emergency stop” switch that disables power to the robot motors and any other potentially hazardous device. An appropriate signal shall be sent when this occurs. See <artifact-link>1734668983</artifact-link>.\n\n## 5.4 Calibration and registration\n\n### 5.4.1 Computation Methods\n\nThe computation methods shall indicate the residual error, so that users can determine how much confidence to place in the result.\n\n## 5.5 Tool tracking\n\n## 5.6 User Interface (Visualization)\n\n### 5.6.1 Unmodified Visualization\n\nThe system shall provide a method for disengaging the research visualization output, so that a clinician can revert to the visualization provided by an unmodified clinical robot (e.g., a clinical daVinci).\n\n## 5.7 Telesurgery application framework\n\n### 5.7.1 Periodic Safety Check\n\nThe application framework shall periodically check all safety-critical subsystems (e.g., by verifying communication integrity) and initiate a safety response (e.g., using function 6.1.1) if a failure is detected.\n\n## 5.8 Volume viewer\n\n# 6. Design Constraints\n\n## 6.1 Operating System\n\nThe system shall be designed to operate on Red Hat Enterprise Linux WS 4. It is desirable for it to work with any type of Linux, with future extension to a real-time Linux such as RTAI.\n\n## 6.2 Programming Language\n\nThe software shall be written in C/C++.\n\n<image-link>1646203177483523742</image-link>\n\nFigure 3: C/C++ Language\n\n## 6.3 Software Libraries\n\n### 6.3.1 CISST Libraries\n\nThe software shall use the CISST libraries.\n\n### 6.3.2 VTK\n\nThe software shall use the Visualization Toolkit (VTK) for visualization of images. The software may optionally use other toolkits that build on, or extend, VTK.\n\n### 6.3.3 Intuitive Surgical\n\nThe tool tracking module shall be based on existing code from Intuitive Surgical.\n\n### 6.3.4 daVinci research API\n\nThe daVinci research API shall be jointly evaluated by ISI and JHU to determine whether to add a requirement to port it to use the CISST operating system abstraction and real-time support libraries (for the thread that manages the data stream).\n",
                start_position: UpdatableValue {
                    previous_value: (12244, 193, 1),
                    current_value: (12244, 193, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (15306, 281, 1),
                    current_value: (15306, 281, 1),
                },
            })],
        });
        assert_eq!(
            results,
            Ok(vec![
                first_block,
                first_feature,
                second_feature,
                first_empty_space,
                first_config_group,
                second_config_group,
                second_empty_space,
                third_feature,
                final_text
            ])
        )
    }
    #[test]
    fn sample_text() {
        let sample_markdown_input = "# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|

";
        let doc_config: ApplicabilityMarkdownLexerConfig =
            ApplicabilityMarkdownLexerConfig::default();
        let results = parse_applicability(
            LocatedSpan::new_extra(
                sample_markdown_input,
                ((0usize, 0, 0usize), (0usize, 0, 0usize)),
            ),
            &doc_config,
        );
        // assert_eq!(results.len(), 71);
        assert_eq!(
            results,
            Ok(vec![
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "# Overview\n\nThis is a test file for using PLE\n\n## Feature Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (0, 1, 1),
                            current_value: (0, 1, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (65, 7, 1),
                            current_value: (65, 7, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Tag 1\n",
                                    start_position: UpdatableValue {
                                        previous_value: (96, 8, 1),
                                        current_value: (96, 8, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (102, 9, 1),
                                        current_value: (102, 9, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (65, 7, 1),
                                    current_value: (65, 7, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (74, 7, 10),
                                    current_value: (118, 10, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (65, 7, 1),
                                current_value: (65, 7, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (118, 10, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (118, 10, 1),
                            current_value: (118, 10, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (119, 11, 1),
                            current_value: (119, 11, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Tag 2\n",
                                    start_position: UpdatableValue {
                                        previous_value: (141, 12, 1),
                                        current_value: (141, 12, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (147, 13, 1),
                                        current_value: (147, 13, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (119, 11, 1),
                                    current_value: (119, 11, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (128, 11, 10),
                                    current_value: (163, 14, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (119, 11, 1),
                                current_value: (119, 11, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (163, 14, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (163, 14, 1),
                            current_value: (163, 14, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (164, 15, 1),
                            current_value: (164, 15, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Included Text\n",
                                    start_position: UpdatableValue {
                                        previous_value: (195, 16, 1),
                                        current_value: (195, 16, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (209, 17, 1),
                                        current_value: (209, 17, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (164, 15, 1),
                                    current_value: (164, 15, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (173, 15, 10),
                                    current_value: (225, 18, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (164, 15, 1),
                                current_value: (164, 15, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (225, 18, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (225, 18, 1),
                            current_value: (225, 18, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (226, 19, 1),
                            current_value: (226, 19, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Excluded".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Excluded Text\n",
                                    start_position: UpdatableValue {
                                        previous_value: (257, 20, 1),
                                        current_value: (257, 20, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (271, 21, 1),
                                        current_value: (271, 21, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (226, 19, 1),
                                    current_value: (226, 19, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (235, 19, 10),
                                    current_value: (287, 22, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (226, 19, 1),
                                current_value: (226, 19, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (287, 22, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n## Else Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (287, 22, 1),
                            current_value: (287, 22, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (304, 26, 1),
                            current_value: (304, 26, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_1",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "Tag 1\n",
                                        start_position: UpdatableValue {
                                            previous_value: (326, 27, 1),
                                            current_value: (326, 27, 1)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (332, 28, 1),
                                            current_value: (332, 28, 1)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (304, 26, 1),
                                        current_value: (304, 26, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (313, 26, 10),
                                        current_value: (332, 28, 1)
                                    }
                                }),
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NestedNotOr(
                                        ApplicabilityNestedNotOrTag(
                                            vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                ApplicabilityTag {
                                                    tag: "APPLIC_1",
                                                    value: "Included".to_string()
                                                },
                                                None
                                            ))],
                                            None,
                                        )
                                    )],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "Not Tag 1\n",
                                        start_position: UpdatableValue {
                                            previous_value: (349, 29, 1),
                                            current_value: (349, 29, 1)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (359, 30, 1),
                                            current_value: (359, 30, 1)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (332, 28, 1),
                                        current_value: (332, 28, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (349, 29, 1),
                                        current_value: (375, 31, 1)
                                    }
                                })
                            ],
                            start_position: UpdatableValue {
                                previous_value: (304, 26, 1),
                                current_value: (304, 26, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (375, 31, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (375, 31, 1),
                            current_value: (375, 31, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (376, 32, 1),
                            current_value: (376, 32, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_2",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "Tag 2\n",
                                        start_position: UpdatableValue {
                                            previous_value: (398, 33, 1),
                                            current_value: (398, 33, 1)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (404, 34, 1),
                                            current_value: (404, 34, 1)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (376, 32, 1),
                                        current_value: (376, 32, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (385, 32, 10),
                                        current_value: (404, 34, 1)
                                    }
                                }),
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NestedNotOr(
                                        ApplicabilityNestedNotOrTag(
                                            vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                ApplicabilityTag {
                                                    tag: "APPLIC_2",
                                                    value: "Included".to_string()
                                                },
                                                None
                                            ))],
                                            None,
                                        )
                                    )],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "Not Tag 2\n",
                                        start_position: UpdatableValue {
                                            previous_value: (421, 35, 1),
                                            current_value: (421, 35, 1)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (431, 36, 1),
                                            current_value: (431, 36, 1)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (404, 34, 1),
                                        current_value: (404, 34, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (421, 35, 1),
                                        current_value: (447, 37, 1)
                                    }
                                })
                            ],
                            start_position: UpdatableValue {
                                previous_value: (376, 32, 1),
                                current_value: (376, 32, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (447, 37, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Boolean Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (447, 37, 1),
                            current_value: (447, 37, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (466, 40, 1),
                            current_value: (466, 40, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![
                                    ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_1",
                                            value: "Included".to_string()
                                        },
                                        None
                                    )),
                                    ApplicTokens::Or(ApplicabilityOrTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_2",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))
                                ],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Included `OR` Excluded Feature\n",
                                    start_position: UpdatableValue {
                                        previous_value: (499, 41, 1),
                                        current_value: (499, 41, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (530, 42, 1),
                                        current_value: (530, 42, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (466, 40, 1),
                                    current_value: (466, 40, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (475, 40, 10),
                                    current_value: (546, 43, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (466, 40, 1),
                                current_value: (466, 40, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (546, 43, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (546, 43, 1),
                            current_value: (546, 43, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (547, 44, 1),
                            current_value: (547, 44, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![
                                    ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_1",
                                            value: "Included".to_string()
                                        },
                                        None
                                    )),
                                    ApplicTokens::And(ApplicabilityAndTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_2",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))
                                ],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Included `AND` Excluded Feature\n",
                                    start_position: UpdatableValue {
                                        previous_value: (580, 45, 1),
                                        current_value: (580, 45, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (612, 46, 1),
                                        current_value: (612, 46, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (547, 44, 1),
                                    current_value: (547, 44, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (556, 44, 10),
                                    current_value: (628, 47, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (547, 44, 1),
                                current_value: (547, 44, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (628, 47, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Substitution Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (628, 47, 1),
                            current_value: (628, 47, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (652, 50, 1),
                            current_value: (652, 50, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_1",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: UpdatableValue {
                                previous_value: (652, 50, 1),
                                current_value: (652, 50, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (668, 51, 1),
                                current_value: (668, 51, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_2",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: UpdatableValue {
                                previous_value: (668, 51, 1),
                                current_value: (668, 51, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (684, 52, 1),
                                current_value: (684, 52, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n- ",
                        start_position: UpdatableValue {
                            previous_value: (684, 52, 1),
                            current_value: (684, 52, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (687, 53, 3),
                            current_value: (687, 53, 3)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_1",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: UpdatableValue {
                                previous_value: (687, 53, 3),
                                current_value: (687, 53, 3)
                            },
                            end_position: UpdatableValue {
                                previous_value: (703, 54, 1),
                                current_value: (703, 54, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "- ",
                        start_position: UpdatableValue {
                            previous_value: (703, 54, 1),
                            current_value: (703, 54, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (705, 54, 3),
                            current_value: (705, 54, 3)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_2",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: UpdatableValue {
                                previous_value: (705, 54, 3),
                                current_value: (705, 54, 3)
                            },
                            end_position: UpdatableValue {
                                previous_value: (721, 55, 1),
                                current_value: (721, 55, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## List Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (721, 55, 1),
                            current_value: (721, 55, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (737, 58, 1),
                            current_value: (737, 58, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "1. Tag 1\n",
                                    start_position: UpdatableValue {
                                        previous_value: (759, 59, 1),
                                        current_value: (759, 59, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (768, 60, 1),
                                        current_value: (768, 60, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (737, 58, 1),
                                    current_value: (737, 58, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (746, 58, 10),
                                    current_value: (784, 61, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (737, 58, 1),
                                current_value: (737, 58, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (784, 61, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "2. Common Row 1\n",
                        start_position: UpdatableValue {
                            previous_value: (784, 61, 1),
                            current_value: (784, 61, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (800, 62, 1),
                            current_value: (800, 62, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "    - Tag 2.1\n",
                                    start_position: UpdatableValue {
                                        previous_value: (822, 63, 1),
                                        current_value: (822, 63, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (836, 64, 1),
                                        current_value: (836, 64, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (800, 62, 1),
                                    current_value: (800, 62, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (809, 62, 10),
                                    current_value: (852, 65, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (800, 62, 1),
                                current_value: (800, 62, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (852, 65, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "3. Tag 2\n    - Tag 2 Subbullet\n",
                                    start_position: UpdatableValue {
                                        previous_value: (874, 66, 1),
                                        current_value: (874, 66, 1)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (905, 68, 1),
                                        current_value: (905, 68, 1)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (852, 65, 1),
                                    current_value: (852, 65, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (861, 65, 10),
                                    current_value: (921, 69, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (852, 65, 1),
                                current_value: (852, 65, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (921, 69, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "4. Common Row 2\n\n## Nested Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (921, 69, 1),
                            current_value: (921, 69, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (955, 73, 1),
                            current_value: (955, 73, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![
                                    ApplicabilityExprKind::Text(Text {
                                        text: "Level 1\n\n",
                                        start_position: UpdatableValue {
                                            previous_value: (977, 74, 1),
                                            current_value: (977, 74, 1)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (986, 76, 1),
                                            current_value: (986, 76, 1)
                                        }
                                    }),
                                    ApplicabilityExprKind::TagContainer(
                                        ApplicabilityExprContainerWithPosition {
                                            contents: vec![ApplicabilityExprKind::Tag(
                                                ApplicabilityExprTag {
                                                    tag: vec![ApplicTokens::NoTag(
                                                        ApplicabilityNoTag(
                                                            ApplicabilityTag {
                                                                tag: "APPLIC_2",
                                                                value: "Included".to_string()
                                                            },
                                                            None
                                                        )
                                                    )],
                                                    kind: ApplicabilityKind::Feature,
                                                    contents: vec![ApplicabilityExprKind::Text(
                                                        Text {
                                                            text: "Level 2\n",
                                                            start_position: UpdatableValue {
                                                                previous_value: (1008, 77, 1),
                                                                current_value: (1008, 77, 1)
                                                            },
                                                            end_position: UpdatableValue {
                                                                previous_value: (1016, 78, 1),
                                                                current_value: (1016, 78, 1)
                                                            }
                                                        }
                                                    )],
                                                    start_position: UpdatableValue {
                                                        previous_value: (986, 76, 1),
                                                        current_value: (986, 76, 1)
                                                    },
                                                    end_position: UpdatableValue {
                                                        previous_value: (995, 76, 10),
                                                        current_value: (1032, 79, 1)
                                                    }
                                                }
                                            )],
                                            start_position: UpdatableValue {
                                                previous_value: (986, 76, 1),
                                                current_value: (986, 76, 1)
                                            },
                                            end_position: UpdatableValue {
                                                previous_value: (0, 0, 0),
                                                current_value: (1032, 79, 1)
                                            }
                                        }
                                    ),
                                ],
                                start_position: UpdatableValue {
                                    previous_value: (955, 73, 1),
                                    current_value: (955, 73, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (964, 73, 10),
                                    current_value: (1048, 80, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (955, 73, 1),
                                current_value: (955, 73, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1048, 80, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Feature and Substitution Test\n\n",
                        start_position: UpdatableValue {
                            previous_value: (1048, 80, 1),
                            current_value: (1048, 80, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1083, 83, 1),
                            current_value: (1083, 83, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![
                                    ApplicabilityExprKind::Text(Text {
                                        text: "Tag1\n\n",
                                        start_position: UpdatableValue {
                                            previous_value: (1105, 84, 1),
                                            current_value: (1105, 84, 1)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (1111, 86, 1),
                                            current_value: (1111, 86, 1)
                                        }
                                    }),
                                    ApplicabilityExprKind::Substitution(
                                        ApplicabilityExprSubstitution {
                                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                ApplicabilityTag {
                                                    tag: "SUB_1",
                                                    value: "Included".to_string()
                                                },
                                                None
                                            ))],
                                            start_position: UpdatableValue {
                                                previous_value: (1111, 86, 1),
                                                current_value: (1111, 86, 1)
                                            },
                                            end_position: UpdatableValue {
                                                previous_value: (1127, 87, 1),
                                                current_value: (1127, 87, 1)
                                            }
                                        }
                                    ),
                                ],
                                start_position: UpdatableValue {
                                    previous_value: (1083, 83, 1),
                                    current_value: (1083, 83, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1092, 83, 10),
                                    current_value: (1143, 88, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1083, 83, 1),
                                current_value: (1083, 83, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1143, 88, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Tables\n\n### Table Rows\n\n| Col A | Col B | Col C | Col D | Col E |\n|---|---|---|---|---:|\n",
                        start_position: UpdatableValue {
                            previous_value: (1143, 88, 1),
                            current_value: (1143, 88, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1236, 95, 1),
                            current_value: (1236, 95, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 0a | 0b | 0c | 0d  | 0e |",
                                    start_position: UpdatableValue {
                                        previous_value: (1257, 95, 22),
                                        current_value: (1257, 95, 22)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1284, 95, 49),
                                        current_value: (1284, 95, 49)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1236, 95, 1),
                                    current_value: (1236, 95, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1245, 95, 10),
                                    current_value: (1300, 96, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1236, 95, 1),
                                current_value: (1236, 95, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1300, 96, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "| 1a | 1b | 1c | 1d | 1e |\n",
                        start_position: UpdatableValue {
                            previous_value: (1300, 96, 1),
                            current_value: (1300, 96, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1327, 97, 1),
                            current_value: (1327, 97, 1)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 2a | 2b | 2c | 2d  | 2e |",
                                    start_position: UpdatableValue {
                                        previous_value: (1348, 97, 22),
                                        current_value: (1348, 97, 22)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1375, 97, 49),
                                        current_value: (1375, 97, 49)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1327, 97, 1),
                                    current_value: (1327, 97, 1)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1336, 97, 10),
                                    current_value: (1391, 98, 1)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1327, 97, 1),
                                current_value: (1327, 97, 1)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1391, 98, 1)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "| 3a | 3b | 3c | 3d | 3e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1391, 98, 1),
                            current_value: (1391, 98, 1)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1420, 99, 3),
                            current_value: (1420, 99, 3)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "4a | 4b | 4c | 4d | 4e",
                                    start_position: UpdatableValue {
                                        previous_value: (1441, 99, 24),
                                        current_value: (1441, 99, 24)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1463, 99, 46),
                                        current_value: (1463, 99, 46)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1420, 99, 3),
                                    current_value: (1420, 99, 3)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1429, 99, 12),
                                    current_value: (1478, 99, 61)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1420, 99, 3),
                                current_value: (1420, 99, 3)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1478, 99, 61)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 5a | 5b | 5c | 5d | 5e |\n\n### Table Cells\n\n| Col A | Col B | Col C | Col D | Col E |\n|---|---|---|---|---:|\n| 1a | 1b | 1c | 1d | 1e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1478, 99, 61),
                            current_value: (1478, 99, 61)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1620, 107, 3),
                            current_value: (1620, 107, 3)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "2a | 2b | 2c | 2d | 2e",
                                    start_position: UpdatableValue {
                                        previous_value: (1641, 107, 24),
                                        current_value: (1641, 107, 24)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1663, 107, 46),
                                        current_value: (1663, 107, 46)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1620, 107, 3),
                                    current_value: (1620, 107, 3)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1629, 107, 12),
                                    current_value: (1678, 107, 61)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1620, 107, 3),
                                current_value: (1620, 107, 3)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1678, 107, 61)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 3a | 3b | 3c | 3d | 3e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1678, 107, 61),
                            current_value: (1678, 107, 61)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1710, 109, 3),
                            current_value: (1710, 109, 3)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "4a | 4b | 4c",
                                    start_position: UpdatableValue {
                                        previous_value: (1731, 109, 24),
                                        current_value: (1731, 109, 24)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1743, 109, 36),
                                        current_value: (1743, 109, 36)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1710, 109, 3),
                                    current_value: (1710, 109, 3)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1719, 109, 12),
                                    current_value: (1758, 109, 51)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1710, 109, 3),
                                current_value: (1710, 109, 3)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1758, 109, 51)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " | 4d | 4e |\n| 5a | 5b | 5c | 5d | 5e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1758, 109, 51),
                            current_value: (1758, 109, 51)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1800, 111, 3),
                            current_value: (1800, 111, 3)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "6a",
                                    start_position: UpdatableValue {
                                        previous_value: (1821, 111, 24),
                                        current_value: (1821, 111, 24)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1823, 111, 26),
                                        current_value: (1823, 111, 26)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1800, 111, 3),
                                    current_value: (1800, 111, 3)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1809, 111, 12),
                                    current_value: (1838, 111, 41)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1800, 111, 3),
                                current_value: (1800, 111, 3)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1838, 111, 41)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " | 6b | 6c | 6d | ",
                        start_position: UpdatableValue {
                            previous_value: (1838, 111, 41),
                            current_value: (1838, 111, 41)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1856, 111, 59),
                            current_value: (1856, 111, 59)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "6e",
                                    start_position: UpdatableValue {
                                        previous_value: (1877, 111, 80),
                                        current_value: (1877, 111, 80)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1879, 111, 82),
                                        current_value: (1879, 111, 82)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1856, 111, 59),
                                    current_value: (1856, 111, 59)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1865, 111, 68),
                                    current_value: (1894, 111, 97)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1856, 111, 59),
                                current_value: (1856, 111, 59)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1894, 111, 97)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 7a | 7b | 7c | 7d | 7e |\n\n### Table Columns\n\n| Col A | ",
                        start_position: UpdatableValue {
                            previous_value: (1894, 111, 97),
                            current_value: (1894, 111, 97)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1954, 116, 11),
                            current_value: (1954, 116, 11)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Col B |",
                                    start_position: UpdatableValue {
                                        previous_value: (1975, 116, 32),
                                        current_value: (1975, 116, 32)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1982, 116, 39),
                                        current_value: (1982, 116, 39)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1954, 116, 11),
                                    current_value: (1954, 116, 11)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1963, 116, 20),
                                    current_value: (1997, 116, 54)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1954, 116, 11),
                                current_value: (1954, 116, 11)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (1997, 116, 54)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " Col C | Col D ",
                        start_position: UpdatableValue {
                            previous_value: (1997, 116, 54),
                            current_value: (1997, 116, 54)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2012, 116, 69),
                            current_value: (2012, 116, 69)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| Col E ",
                                    start_position: UpdatableValue {
                                        previous_value: (2033, 116, 90),
                                        current_value: (2033, 116, 90)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2041, 116, 98),
                                        current_value: (2041, 116, 98)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2012, 116, 69),
                                    current_value: (2012, 116, 69)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2021, 116, 78),
                                    current_value: (2056, 116, 113)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2012, 116, 69),
                                current_value: (2012, 116, 69)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2056, 116, 113)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n|---|",
                        start_position: UpdatableValue {
                            previous_value: (2056, 116, 113),
                            current_value: (2056, 116, 113)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2063, 117, 6),
                            current_value: (2063, 117, 6)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "---|",
                                    start_position: UpdatableValue {
                                        previous_value: (2084, 117, 27),
                                        current_value: (2084, 117, 27)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2088, 117, 31),
                                        current_value: (2088, 117, 31)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2063, 117, 6),
                                    current_value: (2063, 117, 6)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2072, 117, 15),
                                    current_value: (2103, 117, 46)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2063, 117, 6),
                                current_value: (2063, 117, 6)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2103, 117, 46)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "---|---",
                        start_position: UpdatableValue {
                            previous_value: (2103, 117, 46),
                            current_value: (2103, 117, 46)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2110, 117, 53),
                            current_value: (2110, 117, 53)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "|---:",
                                    start_position: UpdatableValue {
                                        previous_value: (2131, 117, 74),
                                        current_value: (2131, 117, 74)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2136, 117, 79),
                                        current_value: (2136, 117, 79)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2110, 117, 53),
                                    current_value: (2110, 117, 53)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2119, 117, 62),
                                    current_value: (2151, 117, 94)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2110, 117, 53),
                                current_value: (2110, 117, 53)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2151, 117, 94)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 1a | ",
                        start_position: UpdatableValue {
                            previous_value: (2151, 117, 94),
                            current_value: (2151, 117, 94)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2160, 118, 8),
                            current_value: (2160, 118, 8)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "1b |",
                                    start_position: UpdatableValue {
                                        previous_value: (2181, 118, 29),
                                        current_value: (2181, 118, 29)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2185, 118, 33),
                                        current_value: (2185, 118, 33)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2160, 118, 8),
                                    current_value: (2160, 118, 8)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2169, 118, 17),
                                    current_value: (2200, 118, 48)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2160, 118, 8),
                                current_value: (2160, 118, 8)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2200, 118, 48)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 1c | 1d ",
                        start_position: UpdatableValue {
                            previous_value: (2200, 118, 48),
                            current_value: (2200, 118, 48)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2209, 118, 57),
                            current_value: (2209, 118, 57)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 1e ",
                                    start_position: UpdatableValue {
                                        previous_value: (2230, 118, 78),
                                        current_value: (2230, 118, 78)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2235, 118, 83),
                                        current_value: (2235, 118, 83)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2209, 118, 57),
                                    current_value: (2209, 118, 57)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2218, 118, 66),
                                    current_value: (2250, 118, 98)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2209, 118, 57),
                                current_value: (2209, 118, 57)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2250, 118, 98)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 2a | ",
                        start_position: UpdatableValue {
                            previous_value: (2250, 118, 98),
                            current_value: (2250, 118, 98)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2259, 119, 8),
                            current_value: (2259, 119, 8)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "2b |",
                                    start_position: UpdatableValue {
                                        previous_value: (2280, 119, 29),
                                        current_value: (2280, 119, 29)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2284, 119, 33),
                                        current_value: (2284, 119, 33)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2259, 119, 8),
                                    current_value: (2259, 119, 8)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2268, 119, 17),
                                    current_value: (2299, 119, 48)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2259, 119, 8),
                                current_value: (2259, 119, 8)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2299, 119, 48)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 2c | 2d ",
                        start_position: UpdatableValue {
                            previous_value: (2299, 119, 48),
                            current_value: (2299, 119, 48)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2308, 119, 57),
                            current_value: (2308, 119, 57)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 2e ",
                                    start_position: UpdatableValue {
                                        previous_value: (2329, 119, 78),
                                        current_value: (2329, 119, 78)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2334, 119, 83),
                                        current_value: (2334, 119, 83)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2308, 119, 57),
                                    current_value: (2308, 119, 57)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2317, 119, 66),
                                    current_value: (2349, 119, 98)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2308, 119, 57),
                                current_value: (2308, 119, 57)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2349, 119, 98)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 3a | ",
                        start_position: UpdatableValue {
                            previous_value: (2349, 119, 98),
                            current_value: (2349, 119, 98)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2358, 120, 8),
                            current_value: (2358, 120, 8)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "3b |",
                                    start_position: UpdatableValue {
                                        previous_value: (2379, 120, 29),
                                        current_value: (2379, 120, 29)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2383, 120, 33),
                                        current_value: (2383, 120, 33)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2358, 120, 8),
                                    current_value: (2358, 120, 8)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2367, 120, 17),
                                    current_value: (2398, 120, 48)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2358, 120, 8),
                                current_value: (2358, 120, 8)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2398, 120, 48)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 3c | 3d ",
                        start_position: UpdatableValue {
                            previous_value: (2398, 120, 48),
                            current_value: (2398, 120, 48)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2407, 120, 57),
                            current_value: (2407, 120, 57)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 3e ",
                                    start_position: UpdatableValue {
                                        previous_value: (2428, 120, 78),
                                        current_value: (2428, 120, 78)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2433, 120, 83),
                                        current_value: (2433, 120, 83)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2407, 120, 57),
                                    current_value: (2407, 120, 57)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2416, 120, 66),
                                    current_value: (2448, 120, 98)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2407, 120, 57),
                                current_value: (2407, 120, 57)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2448, 120, 98)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 3a | ",
                        start_position: UpdatableValue {
                            previous_value: (2448, 120, 98),
                            current_value: (2448, 120, 98)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2457, 121, 8),
                            current_value: (2457, 121, 8)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "3b |",
                                    start_position: UpdatableValue {
                                        previous_value: (2478, 121, 29),
                                        current_value: (2478, 121, 29)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2482, 121, 33),
                                        current_value: (2482, 121, 33)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2457, 121, 8),
                                    current_value: (2457, 121, 8)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2466, 121, 17),
                                    current_value: (2497, 121, 48)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2457, 121, 8),
                                current_value: (2457, 121, 8)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2497, 121, 48)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 3c | 3d ",
                        start_position: UpdatableValue {
                            previous_value: (2497, 121, 48),
                            current_value: (2497, 121, 48)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2506, 121, 57),
                            current_value: (2506, 121, 57)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 3e ",
                                    start_position: UpdatableValue {
                                        previous_value: (2527, 121, 78),
                                        current_value: (2527, 121, 78)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2532, 121, 83),
                                        current_value: (2532, 121, 83)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2506, 121, 57),
                                    current_value: (2506, 121, 57)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2515, 121, 66),
                                    current_value: (2547, 121, 98)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2506, 121, 57),
                                current_value: (2506, 121, 57)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (2547, 121, 98)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n\n",
                        start_position: UpdatableValue {
                            previous_value: (2547, 121, 98),
                            current_value: (2547, 121, 98)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2550, 123, 1),
                            current_value: (2550, 123, 1)
                        }
                    })]
                })
            ])
        );
    }
    #[test]
    fn test_with_else() {
        let sample_markdown_input = r#"
- `Note` ``Feature [FEATURE_A]``
Text that is only included with feature a.
``Feature Else``
Text that is only included when feature a is excluded.
``End Feature``
"#;
        let doc_config: ApplicabilityMarkdownLexerConfig =
            ApplicabilityMarkdownLexerConfig::default();
        let results = parse_applicability(
            LocatedSpan::new_extra(
                sample_markdown_input,
                ((0usize, 0, 0usize), (0usize, 0, 0usize)),
            ),
            &doc_config,
        );
        assert_eq!(
            results,
            Err(
                applicability_parser_errors::ApplicabilityParserError::AstTransformError(
                    AstTransformError::UnexpectedEndFeature(((0, 0, 0), (0, 0, 0)))
                )
            )
        )
    }
}
