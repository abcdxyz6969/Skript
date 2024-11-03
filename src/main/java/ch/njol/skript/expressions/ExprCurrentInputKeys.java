package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInputEvent;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.InputKey;

import java.util.ArrayList;
import java.util.List;

@Name("Player Input Keys")
@Description("Get the current input keys of a player.")
@Examples("broadcast \"%player% is pressing %current input keys of player%\"")
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21.3+")
public class ExprCurrentInputKeys extends PropertyExpression<Player, InputKey> {

	static {
		if (Skript.classExists("org.bukkit.Input"))
			register(ExprCurrentInputKeys.class, InputKey.class, "[current] (inputs|input keys)", "players");
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<? extends Player>) expressions[0]);
		return true;
	}

	@Override
	protected InputKey[] get(Event event, Player[] source) {
		Player eventPlayer = getTime() == EventValues.TIME_NOW && event instanceof PlayerInputEvent inputEvent ? inputEvent.getPlayer() : null;
		List<InputKey> inputKeys = new ArrayList<>();
		for (Player player : source) {
			if (player.equals(eventPlayer)) {
				inputKeys.addAll(InputKey.fromInput(((PlayerInputEvent) event).getInput()));
			} else {
				inputKeys.addAll(InputKey.fromInput(player.getCurrentInput()));
			}
		}
		return inputKeys.toArray(new InputKey[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends InputKey> getReturnType() {
		return InputKey.class;
	}

	@Override
	public boolean setTime(int time) {
		return time != EventValues.TIME_FUTURE && setTime(time, PlayerInputEvent.class);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "current input keys of " + getExpr().toString(event, debug);
	}

}
