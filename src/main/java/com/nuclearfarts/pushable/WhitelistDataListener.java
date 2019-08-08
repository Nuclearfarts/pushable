package com.nuclearfarts.pushable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class WhitelistDataListener implements SimpleResourceReloadListener<Set<String>> {

	public static final Identifier WHITELIST_LOCATION = new Identifier("pushable", "config/whitelist.json");
	public static final Identifier BLACKLIST_LOCATION = new Identifier("pushable", "config/blacklist.json");
	private Gson gson = new Gson();

	@Override
	public Identifier getFabricId() {
		return new Identifier("pushable", "whitelist_listener");
	}

	@Override
	public CompletableFuture<Set<String>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Set<String> blacklist = loadBlocklistFile(manager.getResource(BLACKLIST_LOCATION));
				Set<String> whitelist = new HashSet<String>();
				manager.getAllResources(WHITELIST_LOCATION).stream().map(this::loadBlocklistFile).forEach(whitelist::addAll);
				whitelist.removeAll(blacklist);
				return whitelist;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, executor);
	}

	@Override
	public CompletableFuture<Void> apply(Set<String> data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			Pushable.WHITELIST.clear();
			data.stream().map(Identifier::new).map(Registry.BLOCK::get).forEach(Pushable.WHITELIST::add);
		}, executor);
	}
	
	private Set<String> loadBlocklistFile(Resource from) {
		return gson.fromJson(new InputStreamReader(from.getInputStream()), BlocklistFile.class).values;
	}

}
