package dev.hephaestus.relentlessdead.impl.entitest.ai.goal;

import dev.hephaestus.relentlessdead.impl.entitest.PathfindingEntity;

public class ZombieAttackGoal extends MeleeAttackGoal {
	private final PathfindingEntity zombie;
	private int ticks;

	public ZombieAttackGoal(PathfindingEntity zombie, double speed, boolean pauseWhenMobIdle) {
		super(zombie, speed, pauseWhenMobIdle);
		this.zombie = zombie;
	}

	public void start() {
		super.start();
		this.ticks = 0;
	}

	public void stop() {
		super.stop();
		this.zombie.setAttacking(false);
	}

	public void tick() {
		super.tick();
		++this.ticks;
		if (this.ticks >= 5 && this.method_28348() < this.method_28349() / 2) {
			this.zombie.setAttacking(true);
		} else {
			this.zombie.setAttacking(false);
		}

	}
}
