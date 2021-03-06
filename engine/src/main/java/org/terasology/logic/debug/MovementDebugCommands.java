/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.debug;

import org.terasology.asset.Asset;
import org.terasology.asset.AssetUri;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.internal.PojoPrefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.characters.events.SetMovementModeEvent;
import org.terasology.logic.console.Command;
import org.terasology.logic.console.CommandParam;
import org.terasology.logic.health.HealthComponent;
import org.terasology.network.ClientComponent;

/**
 * @author Immortius
 */
@RegisterSystem
public class MovementDebugCommands extends BaseComponentSystem {

    @Command(shortDescription = "Grants flight and movement through walls", runOnServer = true)
    public String ghost(EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        clientComp.character.send(new SetMovementModeEvent(MovementMode.GHOSTING));

        return "Ghost mode toggled";
    }

    @Command(shortDescription = "Grants flight", runOnServer = true)
    public String flight(EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        clientComp.character.send(new SetMovementModeEvent(MovementMode.FLYING));

        return "Flight mode toggled";
    }


    @Command(shortDescription = "Set speed multiplier", helpText = "Set speedMultiplier", runOnServer = true)
    public String setSpeedMultiplier(@CommandParam("amount") float amount, EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        if (move != null) {
            float oldSpeedMultipler = move.speedMultiplier;
            move.speedMultiplier = amount;
            clientComp.character.saveComponent(move);

            return "Speed multiplier set to " + amount + " (was " + oldSpeedMultipler + ")";
        }
        
        return "";
    }

    @Command(shortDescription = "Set jump speed", runOnServer = true)
    public String setJumpSpeed(@CommandParam("amount") float amount, EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        if (move != null) {
            float oldSpeed = move.jumpSpeed;
            move.jumpSpeed = amount;
            clientComp.character.saveComponent(move);
            
            return "Jump speed set to " + amount + " (was " + oldSpeed + ")";
        }
        
        return "";
    }

    @Command(shortDescription = "Show your Movement stats")
    public String showMovement(EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        if (move != null) {
            return "Your SpeedMultiplier:" + move.speedMultiplier + " JumpSpeed:"
                    + move.jumpSpeed + " SlopeFactor:"
                    + move.slopeFactor + " RunFactor:" + move.runFactor;
        }
        return "You're dead I guess.";
    }

    @Command(shortDescription = "Go really fast", runOnServer = true)
    public String hspeed(EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        if (move != null) {
            move.speedMultiplier = 10f;
            move.jumpSpeed = 24f;
            clientComp.character.saveComponent(move);
            
            return "High-speed mode activated";
        }
        
        return "";
    }

    @Command(shortDescription = "Jump really high", runOnServer = true)
    public String hjump(EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        HealthComponent health = clientComp.character.getComponent(HealthComponent.class);
        if (health != null && move != null) {
            move.jumpSpeed = 75f;
            health.fallingDamageSpeedThreshold = 85f;
            health.excessSpeedDamageMultiplier = 2f;
            clientComp.character.saveComponent(health);
            clientComp.character.saveComponent(move);
            
            return "High-jump mode activated";
        }
        
        return "";
    }

    @Command(shortDescription = "Restore normal speed values", runOnServer = true)
    public String restoreSpeed(EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);

        Asset<?> asset = Assets.get(new AssetUri("prefab:engine:player"));
        CharacterMovementComponent moveDefault = ((PojoPrefab) asset).getComponent(CharacterMovementComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        if (move != null && moveDefault != null) {
            move.jumpSpeed = moveDefault.jumpSpeed;
            move.speedMultiplier = moveDefault.speedMultiplier;
            move.runFactor = moveDefault.runFactor;
            move.stepHeight = moveDefault.stepHeight;
            move.slopeFactor = moveDefault.slopeFactor;
            move.distanceBetweenFootsteps = moveDefault.distanceBetweenFootsteps;
            clientComp.character.saveComponent(move);
        }

        HealthComponent healthDefault = ((PojoPrefab) asset).getComponent(HealthComponent.class);
        HealthComponent health = clientComp.character.getComponent(HealthComponent.class);
        if(health != null && healthDefault != null){
            health.fallingDamageSpeedThreshold = healthDefault.fallingDamageSpeedThreshold;
            health.horizontalDamageSpeedThreshold = healthDefault.horizontalDamageSpeedThreshold;
            health.excessSpeedDamageMultiplier = healthDefault.excessSpeedDamageMultiplier;
            clientComp.character.saveComponent(health);
        }

        return "Normal speed values restored";
    }

    @Command(shortDescription = "Toggles the maximum slope the player can walk up", runOnServer = true)
    public String sleigh(EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        if (move != null) {
            float oldFactor = move.slopeFactor;
            if (move.slopeFactor > 0.7f) {
                move.slopeFactor = 0.6f;
            } else {
                move.slopeFactor = 0.9f;
            }
            clientComp.character.saveComponent(move);
            return "Slope factor is now " + move.slopeFactor + " (was " + oldFactor + ")";
        }
        return "";
    }

    @Command(shortDescription = "Sets the height the player can step up", runOnServer = true)
    public String stepHeight(@CommandParam("height") float amount, EntityRef client) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        CharacterMovementComponent move = clientComp.character.getComponent(CharacterMovementComponent.class);
        if (move != null) {
            float prevStepHeight = move.stepHeight;
            move.stepHeight = amount;
            clientComp.character.saveComponent(move);
            
            return "Ground friction set to " + amount + " (was " + prevStepHeight + ")";
        }
        
        return "";
    }
}
