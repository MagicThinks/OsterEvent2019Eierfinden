import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ClientEvents {
	
	//variables
	//prevents from doppel sending
	ArrayList<String> array = new ArrayList<String>();
	//Particle on and off
	boolean particle = true;
	
	@SubscribeEvent
	public void onRender(ClientTickEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		WorldClient world = mc.theWorld;
		
		if(e.phase == Phase.END) {
			Item wantedItem = Items.spawn_egg;
			try {
				List<Entity> entities = world.loadedEntityList;
				//Go through all entities
				for (int i = 0; i < entities.size(); i++) {
					if (entities.get(i) instanceof EntityItem) {
	 					EntityItem item = (EntityItem) entities.get(i);
	 					//See if the item is a spawn egg
	 					if (item.getEntityItem().getItem() == Items.spawn_egg) {
	 						//Coords vom Egg
		 					int x = item.getPosition().getX();
		 					int y = item.getPosition().getY();
		 					int z = item.getPosition().getZ();
		 					if(!array.contains(x+":"+y+":"+z)) {
		 						//One-time sending the coordinates from the egg
			 					addMSG(EnumChatFormatting.GRAY+"X: "+EnumChatFormatting.GREEN+ x + 
			 							EnumChatFormatting.GRAY+" Y: "+EnumChatFormatting.GREEN+  y + 
			 							EnumChatFormatting.GRAY+" Z: "+EnumChatFormatting.GREEN+ z, 
			 							mc);
//			 					Write(x, y, z);
			 					array.add(x+":"+y+":"+z);
		 					}	
		 					if(particle) {
		 						//Generating particles to better find the Egg
			 					world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x  , y + 1D, z, 0D, 0D, 0D, new int[0]);
			 					world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x  , y + 2D, z, 0D, 0D, 0D, new int[0]);
			 					world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x  , y + 3D, z, 0D, 0D, 0D, new int[0]);
			 					world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x  , y + 4D, z, 0D, 0D, 0D, new int[0]);
			 					world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x  , y + 5D, z, 0D, 0D, 0D, new int[0]);
			 					world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x  , y + 1D, z, 0D, 0D, 0D, new int[0]);
		 					};	
	 					}
					}
				}
			}catch (Exception ex) {
				// TODO: handle exception
			}
		}
	}
	
	/**
	 * Save Coords to File. Here for Windows
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @throws IOException File Exception
	 */
	private void Write(int x, int y, int z) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter("C:\\\\temp\\\\coords.txt", true), true);
        pw.println("x: "+ x +" y: "+y + " z: "+ z);
        pw.close();
	}
	
	/**
	 * Messages can be seen by everyone ..
	 * No better Class found yet
	 * Best via /msg send to a friend
	 * @param e
	 */
	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent e) {
		// Get Minecraft
		Minecraft mc = Minecraft.getMinecraft();
		// Get Received Message
		IChatComponent msg = e.message;
		String conmsg = msg.toString();
		//Paar fürs Ei
		if(conmsg.contains(".list")) {
			int a = array.size();
			if(a > 1) {
				addMSG(EnumChatFormatting.GOLD +""+ (a) + EnumChatFormatting.GRAY +" Eier wurden gefunden!");
			}else if(a == 1){
				addMSG(EnumChatFormatting.GOLD +""+ (a) + EnumChatFormatting.GRAY +" Ei wurden gefunden!");
			}else if(a < 1){
				addMSG(EnumChatFormatting.GRAY +"Keine Eier wurden gefunden!");
			}
		}
		if(conmsg.contains(".clear")){
			array.clear();
			addMSG(EnumChatFormatting.GRAY + "Liste wurde "+ EnumChatFormatting.GREEN+ "erfolgreich"+ EnumChatFormatting.GRAY +" zurückgesetzt!");
		}
		if(conmsg.contains(".on")){
			particle = true;
			addMSG(EnumChatFormatting.GRAY + "Partikel wurden "+ EnumChatFormatting.GREEN+ "erfolgreich"+ EnumChatFormatting.GREEN +" angeschalten!");
		}
		if(conmsg.contains(".off")){
			particle = false;
			addMSG(EnumChatFormatting.GRAY + "Partikel wurden "+ EnumChatFormatting.GREEN+ "erfolgreich"+ EnumChatFormatting.RED +" ausgeschalten!");
		}
		if(conmsg.contains(".help")) {
			addMSG(EnumChatFormatting.DARK_GRAY+"Commands"+EnumChatFormatting.GRAY+": "+
					"\n"+EnumChatFormatting.GRAY+".list"+
					"\n"+EnumChatFormatting.GRAY+".clear"+
					"\n"+EnumChatFormatting.GRAY+".on"+
					"\n"+EnumChatFormatting.GRAY+".off");	
		}
	}
	/**
	 * Send only to own Player a MSG
	 * @param msg String for the MSG
	 * @param mc Minecraft
	 */
	private void addMSG(String msg, Minecraft mc) {
		mc.thePlayer.addChatComponentMessage(new ChatComponentText(msg));
	}
	/**
	 * Send only to own Player a MSG
	 * @param msg String for the MSG
	 */
	private void addMSG(String msg) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.thePlayer.addChatComponentMessage(new ChatComponentText(msg));
	}
	/**
	 * Send a Message to all Players form the MC Player
	 * @param msg String Message
	 * @param mc Minecraft
	 */
	private void sendMSG(String msg, Minecraft mc) {
		mc.thePlayer.sendChatMessage(msg);
	}
}
