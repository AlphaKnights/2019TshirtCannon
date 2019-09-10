/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;

/**
 * * This is the code for the T-Shirt Cannon. Works pretty good.
 * 
 * Basic info: Drives with logitech flight stick, air compressor with button 12,
 * fire with triger, thumb button, and button 5/6 for top and bottom array.
 */
public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private SpeedControllerGroup leftSideSpeedControllerGroup;
  private SpeedControllerGroup rightSideSpeedControllerGroup;

  private Solenoid Solenoid_1;
  private Solenoid Solenoid_2;
  private Solenoid Solenoid_3;
  private Solenoid Solenoid_4;
  private Solenoid Solenoid_5;
  private Solenoid Solenoid_6;

  private static final int kJoystickChannel = 0;
  private static final int kGyroChannel = 0;
  private Joystick m_stick;
  private AnalogGyro m_gyro;

  private Solenoid[] topSolonoids;
  private Solenoid[] bottomSolonoids;

  private Compressor TestCompressor;

  @Override
  public void robotInit() {
    Solenoid_1 = new Solenoid(0);
    Solenoid_2 = new Solenoid(1);
    Solenoid_3 = new Solenoid(2);
    Solenoid_4 = new Solenoid(3);
    Solenoid_5 = new Solenoid(4);
    Solenoid_6 = new Solenoid(5);
    TestCompressor = new Compressor(0);
    TestCompressor.setClosedLoopControl(false);

    m_stick = new Joystick(kJoystickChannel);

    leftSideSpeedControllerGroup = new SpeedControllerGroup(new WPI_TalonSRX(1), new WPI_TalonSRX(2));
    rightSideSpeedControllerGroup = new SpeedControllerGroup(new WPI_TalonSRX(3), new WPI_TalonSRX(4));
    m_myRobot = new DifferentialDrive(leftSideSpeedControllerGroup, rightSideSpeedControllerGroup);

    topSolonoids = new Solenoid[] { Solenoid_1, Solenoid_2, Solenoid_3 };
    bottomSolonoids = new Solenoid[] { Solenoid_4, Solenoid_5, Solenoid_6 };
    m_gyro = new AnalogGyro(kGyroChannel);
    System.out.println("rgirhov");
  }

  @Override
  public void teleopPeriodic() {
    private boolean isEnabled = false;

    // Make robit go
    double[] movementList = adjustJoystickInput(-m_stick.getY(), m_stick.getX(), m_stick.getThrottle());
    //see adjustJoystickInput()
    m_myRobot.arcadeDrive(movementList[0], movementList[1]);
    System.out.println(m_gyro.getAngle());

    //Toggle system for compresser: press 7,8,9,10 with 4 fingers on your left hand and pull trigger with right.
    if (isEnabled == false && m_stick.getRawButton(7) && m_stick.getRawButton(8) && m_stick.getRawButton(9) && m_stick.getRawButton(10) && m_stick.getRawButton(1)) {
      isEnabled = true;
    } else if (isEnabled && m_stick.getRawButton(7) && m_stick.getRawButton(8) && m_stick.getRawButton(9) && m_stick.getRawButton(10) && m_stick.getRawButton(1)) {
      isEnabled = false;
    }
    // Alternatively you can just hold 12 like the loser Jake thinks you are
    if (isEnabled || m_stick.getRawButton(12)) {
        testCompressor.setClosedLoopControl(true);
    }


    //// Firing logic stuff, most of it is obsolete, see fireCannon function
    // fireCannon(Solenoid_1, m_stick, 8);
    // fireCannon(Solenoid_2, m_stick, 10);
    // fireCannon(Solenoid_3, m_stick, 12);
    // fireCannon(Solenoid_4, m_stick, 7);
    // fireCannon(Solenoid_5, m_stick, 9);
    // fireCannon(Solenoid_6, m_stick, 11);


    //? idk what these comments below are
    // Logic to control trigering is inside
    // TODO: Move the logic out here, makes more sense.
    
    //will fire top cannons with button 5 or bottom with button 6, see how below.
    fireTrio(topSolonoids, m_stick, 5);
    fireTrio(bottomSolonoids, m_stick, 6);
  }

  @Override
  public void testInit() {
    TestCompressor = new Compressor(0);
    TestCompressor.setClosedLoopControl(true);
  }

  @Override
  public void testPeriodic() {
    System.out.print(String.format("Compressor Status: {0}", TestCompressor.getPressureSwitchValue()));
    System.out.print(String.format("Compressor Status: {0}", TestCompressor.getCompressorCurrent()));
  }

  private void fireTrio(Solenoid[] solonoidsToFire, Joystick joystick, int secondaryButton) {
    // Should be called every update loop, checks for trigger(1), thumb button(2), and secondaryButton is called earlier in the code,
    // and it can be either 5 or 6.  Requires 2 hands to realistically launch it, for safety.
    if (joystick.getRawButton(1) && joystick.getRawButton(2) && joystick.getRawButton(secondaryButton)) {
      solonoidsToFire[0].set(true);
      solonoidsToFire[1].set(true);
      solonoidsToFire[2].set(true);
    } else {
      solonoidsToFire[0].set(false);
      solonoidsToFire[1].set(false);
      solonoidsToFire[2].set(false);
    }
  }

  //? movementList[] wants to have 2 values stored in it as a matrix, but is dependant on 3 variables, which are the y and z power, and throttle
  //? This function, when you set movementList to it and call the 3 variables, will calculate y and z power for the moters
  //? and set the matrix to the correct values that can be used in arcadeDrive().

  private double[] adjustJoystickInput(double yPower, double zPower, double Throttle) {
    // Makes the throttle work on a sqrt curve as to make controlling easier.
    double adjustedThrottle = Math.sqrt(((-1 * Throttle) + 1) / 2); 
    double yPowerOut = yPower * adjustedThrottle;
    double zPowerOut = zPower * adjustedThrottle * 0.85;   
    //sets movementList[] values to y and z power, as object 1 and 2 in the matrix respectively   
    double[] outputList = new double[] { yPowerOut, zPowerOut };
    return outputList;
  }

  
/*
  //! Obsolete, do not use. 
  //! This used to fire single cannons, but we rewired pneumatics so that doesn't exist anymore.

  public void fireCannon(Solenoid solonoidToFire, Joystick joystick, int secondaryButton) {
    if (joystick.getRawButton(1) && joystick.getRawButton(secondaryButton)) {
      solonoidToFire.set(true);
    } else {
      solonoidToFire.set(false);
    }
  }

*/
}
 
