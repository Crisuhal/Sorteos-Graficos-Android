package com.sorteosgraficos.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends Activity {
    private LinearLayout main, content;
    private FrameLayout container;
    private final int purple = Color.rgb(92, 72, 210);
    private final int background = Color.rgb(246, 247, 252);
    private final Random random = new Random();
    private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(purple);
        getWindow().setNavigationBarColor(Color.WHITE);
        createHome();
        showNumbers();
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(Color.rgb(40,40,55));
        t.setPadding(dp(6),dp(8),dp(6),dp(8));
        return t;
    }
    private Button button(String value) {
        Button b = new Button(this); b.setText(value); b.setTextColor(Color.WHITE);
        b.setTextSize(14); b.setAllCaps(false); b.setBackgroundColor(purple); return b;
    }
    private EditText field(String hint, boolean multiline, boolean numeric) {
        EditText e = new EditText(this); e.setHint(hint); e.setTextSize(16);
        e.setPadding(dp(12),dp(8),dp(12),dp(8)); e.setBackgroundColor(Color.WHITE);
        if (multiline) {
            e.setGravity(Gravity.TOP|Gravity.LEFT); e.setMinLines(4);
            e.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        } else {
            e.setSingleLine(true);
            e.setInputType(numeric ? InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL : InputType.TYPE_CLASS_TEXT);
            e.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        return e;
    }
    private void createHome() {
        main = new LinearLayout(this); main.setOrientation(LinearLayout.VERTICAL); main.setBackgroundColor(background);
        TextView header = text("SORTEOS PRO",22); header.setTextColor(Color.WHITE); header.setGravity(Gravity.CENTER);
        header.setTypeface(null,1); header.setBackgroundColor(purple); header.setPadding(0,dp(16),0,dp(16)); main.addView(header);
        LinearLayout menu = new LinearLayout(this); menu.setOrientation(LinearLayout.HORIZONTAL); menu.setBackgroundColor(Color.WHITE);
        final String[] names = {"NÚMEROS","TEXTO","GRÁFICOS"};
        for (int i=0;i<names.length;i++) {
            final int page = i; Button b = new Button(this); b.setText(names[i]); b.setTextSize(10); b.setAllCaps(false); b.setPadding(0,0,0,0);
            menu.addView(b,new LinearLayout.LayoutParams(0,dp(52),1));
            b.setOnClickListener(new View.OnClickListener(){ public void onClick(View v){ if(page==0)showNumbers(); else if(page==1)showText(); else showCharts(); }});
        }
        main.addView(menu); container = new FrameLayout(this);
        main.addView(container,new LinearLayout.LayoutParams(-1,0,1)); setContentView(main);
    }
    private void page(String title) {
        container.removeAllViews(); ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(16),dp(12),dp(16),dp(24));
        TextView heading = text(title,22); heading.setTypeface(null,1); content.addView(heading);
        scroll.addView(content); container.addView(scroll);
    }
    private void label(String value) { content.addView(text(value,15)); }
    private void addField(EditText e) { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,dp(54)); p.setMargins(0,0,0,dp(5)); content.addView(e,p); }
    private void addButton(Button b) { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,dp(54)); p.setMargins(0,dp(12),0,dp(12)); content.addView(b,p); }
    private TextView resultBox(String value, int size) {
        TextView r=text(value,size); r.setBackgroundColor(Color.WHITE); r.setPadding(dp(14),dp(14),dp(14),dp(14));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(8),0,dp(8)); content.addView(r,p); return r;
    }

    private void showNumbers() {
        page("🔢 Generador de números"); label("Número mínimo:"); final EditText min=field("Ejemplo: 1",false,true); addField(min);
        label("Número máximo:"); final EditText max=field("Ejemplo: 100",false,true); addField(max);
        label("Cantidad de números (máximo 500):"); final EditText quantity=field("Ejemplo: 10",false,true); addField(quantity);
        label("Números por grupo (0 = sin grupos):"); final EditText group=field("Ejemplo: 5",false,true); addField(group);
        final CheckBox unique=new CheckBox(this); unique.setText("No permitir números repetidos"); unique.setChecked(true); content.addView(unique);
        final TextView result=resultBox("Acá aparecerán los resultados.",17);
        Button generate=button("🎲 GENERAR SORTEO"); addButton(generate);
        generate.setOnClickListener(new View.OnClickListener(){ public void onClick(View v){
            try {
                long a=Long.parseLong(min.getText().toString().trim()); long b=Long.parseLong(max.getText().toString().trim());
                int q=Integer.parseInt(quantity.getText().toString().trim()); String gs=group.getText().toString().trim(); int g=gs.length()==0?0:Integer.parseInt(gs);
                if(a>b||q<1||q>500||g<0||g>500) throw new Exception();
                BigInteger range=BigInteger.valueOf(b).subtract(BigInteger.valueOf(a)).add(BigInteger.ONE);
                if(range.compareTo(BigInteger.valueOf(Long.MAX_VALUE))>0) throw new Exception();
                if(unique.isChecked()&&BigInteger.valueOf(q).compareTo(range)>0) throw new Exception();
                ArrayList<Long> nums=new ArrayList<Long>(); HashSet<Long> used=new HashSet<Long>(); long bound=range.longValue();
                while(nums.size()<q){ long n=a+nextLong(bound); if(!unique.isChecked()||used.add(n)) nums.add(n); }
                StringBuilder out=new StringBuilder();
                if(g>0){ for(int i=0;i<nums.size();i+=g){ out.append("GRUPO ").append(i/g+1).append("\n"); for(int j=i;j<Math.min(i+g,nums.size());j++){ if(j>i)out.append("   "); out.append(nums.get(j)); } out.append("\n\n"); } }
                else { for(int i=0;i<nums.size();i++) out.append(i+1).append(".  ").append(nums.get(i)).append("\n"); }
                result.setText(out.toString());
            } catch(Exception ex){ result.setText("Revisá los valores: mínimo y máximo válidos, cantidad entre 1 y 500 y grupo desde 0."); }
        }});
    }
    // Uniform random number in [0,bound), using rejection to avoid modulo bias.
    private long nextLong(long bound) {
        long bits, value; do { bits=random.nextLong()>>>1; value=bits%bound; } while(bits-value+(bound-1)<0L); return value;
    }

    private void showText() {
        page("📝 Sorteo de texto"); label("Escribí un participante por línea:");
        final EditText input=field("Juan\nPedro\nMaría\nCarlos\nAna",true,false); LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,dp(150)); content.addView(input,ip);
        label("Cantidad de ganadores:"); final EditText quantity=field("Ejemplo: 1",false,true); addField(quantity);
        final CheckBox unique=new CheckBox(this); unique.setText("No repetir participantes"); unique.setChecked(true); content.addView(unique);
        final TextView result=resultBox("Acá aparecerán los ganadores.",18); Button generate=button("🎲 SORTEAR TEXTO"); addButton(generate);
        generate.setOnClickListener(new View.OnClickListener(){ public void onClick(View v){
            try {
                String[] lines=input.getText().toString().split("\\r?\\n"); ArrayList<String> items=new ArrayList<String>();
                for(String line:lines) if(!line.trim().isEmpty()) items.add(line.trim());
                int q=Integer.parseInt(quantity.getText().toString().trim()); if(q<1||q>500||items.size()==0) throw new Exception();
                if(unique.isChecked()&&q>items.size()) throw new Exception();
                ArrayList<String> pool=new ArrayList<String>(items); Collections.shuffle(pool,random);
                StringBuilder out=new StringBuilder("🏆 RESULTADOS\n\n");
                for(int i=0;i<q;i++){ int index=unique.isChecked()?i:random.nextInt(pool.size()); out.append(i+1).append(". ").append(pool.get(index)).append("\n\n"); }
                result.setText(out.toString());
            } catch(Exception ex){ result.setText("Poné participantes y una cantidad válida. Sin repetición, no puede superar la cantidad de nombres."); }
        }});
    }

    private void showCharts() {
        page("📊 Generador de gráficos"); label("Ingresá una categoría y un valor por línea:");
        final EditText input=field("Ventas, 120\nGastos, 80\nIngresos, 200",true,false); content.addView(input,new LinearLayout.LayoutParams(-1,dp(135)));
        label("Tipo de gráfico:"); final Spinner spinner=new Spinner(this);
        String[] types={"Barras","Líneas","Circular"}; ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,types); adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); spinner.setAdapter(adapter); content.addView(spinner);
        final ChartView chart=new ChartView(); LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(320)); cp.setMargins(0,dp(10),0,dp(8)); content.addView(chart,cp);
        final TextView error=text("Formato: nombre, valor",13); error.setTextColor(Color.DKGRAY); content.addView(error);
        Button generate=button("📊 CREAR GRÁFICO"); addButton(generate);
        generate.setOnClickListener(new View.OnClickListener(){ public void onClick(View v){
            try {
                ArrayList<String> labels=new ArrayList<String>(); ArrayList<Float> values=new ArrayList<Float>(); String[] lines=input.getText().toString().split("\\r?\\n");
                for(String raw:lines){ String line=raw.trim(); if(line.length()==0)continue; int sep=Math.max(line.lastIndexOf(','),line.lastIndexOf(';')); if(sep<0)sep=line.lastIndexOf(':'); if(sep<1)throw new Exception();
                    String name=line.substring(0,sep).trim(); String number=line.substring(sep+1).trim().replace(',','.'); float val=Float.parseFloat(number);
                    if(name.length()==0||Float.isNaN(val)||Float.isInfinite(val))throw new Exception(); labels.add(name); values.add(val);
                }
                int type=spinner.getSelectedItemPosition(); if(labels.size()<1||labels.size()>30)throw new Exception();
                if(type==2){ boolean any=false; for(Float value:values){ if(value<0)throw new Exception(); if(value>0)any=true; } if(!any)throw new Exception(); }
                chart.setData(labels,values,type); error.setText("Gráfico actualizado · "+labels.size()+" categorías");
            } catch(Exception ex){ error.setText("Revisá los datos. Usá «Nombre, valor»; para el circular, valores positivos."); Toast.makeText(MainActivity.this,"No pude leer esos datos. Revisá el formato.",Toast.LENGTH_LONG).show(); }
        }});
    }

    private class ChartView extends View {
        private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private ArrayList<String> labels=new ArrayList<String>(); private ArrayList<Float> values=new ArrayList<Float>(); private int type=0;
        private final int[] colors={Color.rgb(92,72,210),Color.rgb(0,160,150),Color.rgb(255,145,70),Color.rgb(230,80,130),Color.rgb(60,140,220),Color.rgb(150,100,200),Color.rgb(220,180,50)};
        ChartView(){ super(MainActivity.this); setBackgroundColor(Color.WHITE); }
        void setData(ArrayList<String> l,ArrayList<Float> v,int t){ labels=new ArrayList<String>(l); values=new ArrayList<Float>(v); type=t; invalidate(); }
        private void drawText(Canvas c,String s,float x,float y,int size,int color){ p.setColor(color); p.setTextSize(dp(size)); p.setStyle(Paint.Style.FILL); c.drawText(s,x,y,p); }
        protected void onDraw(Canvas c){ super.onDraw(c); if(values.size()==0){drawText(c,"Ingresá datos y tocá crear gráfico",dp(12),dp(160),14,Color.DKGRAY);return;} if(type==2)drawPie(c);else drawCartesian(c); }
        private void drawCartesian(Canvas c){
            float w=getWidth(),h=getHeight(),left=dp(38),right=w-dp(12),top=dp(24),bottom=h-dp(75); if(right<=left||bottom<=top)return;
            float min=0,max=0; for(Float f:values){min=Math.min(min,f);max=Math.max(max,f);} if(max==min)max=min+1;
            p.setColor(Color.LTGRAY);p.setStrokeWidth(dp(1));p.setStyle(Paint.Style.STROKE); for(int i=0;i<5;i++){float y=top+(bottom-top)*i/4f;c.drawLine(left,y,right,y,p);} p.setStyle(Paint.Style.FILL);
            float range=max-min, zero=bottom-(0-min)/range*(bottom-top), step=(right-left)/values.size();
            if(type==0){ for(int i=0;i<values.size();i++){float val=values.get(i);float x=left+i*step+step*.18f;float y=bottom-(val-min)/range*(bottom-top);p.setColor(colors[i%colors.length]);c.drawRect(x,Math.min(y,zero),x+step*.64f,Math.max(y,zero),p);drawLabel(c,labels.get(i),x+step*.32f,bottom+dp(18),step);drawText(c,String.format(java.util.Locale.US,"%.1f",val),x,boundedY(y-dp(4),top+dp(10),bottom),9,Color.DKGRAY);} }
            else {p.setColor(purple);p.setStrokeWidth(dp(3));p.setStyle(Paint.Style.STROKE);float px=0,py=0;for(int i=0;i<values.size();i++){float x=left+(values.size()==1?step/2:i*step+step/2);float y=bottom-(values.get(i)-min)/range*(bottom-top);if(i>0)c.drawLine(px,py,x,y,p);px=x;py=y;p.setStyle(Paint.Style.FILL);p.setColor(colors[i%colors.length]);c.drawCircle(x,y,dp(5),p);drawLabel(c,labels.get(i),x,bottom+dp(18),step);p.setStyle(Paint.Style.STROKE);p.setColor(purple);}p.setStyle(Paint.Style.FILL);}
        }
        private float boundedY(float y,float low,float high){return Math.max(low,Math.min(high,y));}
        private void drawLabel(Canvas c,String label,float x,float y,float width){String s=label;while(s.length()>1&&p.measureText(s)>width) s=s.substring(0,s.length()-1);if(!s.equals(label)&&s.length()>1)s=s.substring(0,s.length()-1)+"…"; drawText(c,s,x-p.measureText(s)/2,y,9,Color.DKGRAY);}
        private void drawPie(Canvas c){
            float total=0; for(Float v:values) total+=v; if(total<=0){drawText(c,"El gráfico circular necesita valores positivos",dp(12),dp(160),13,Color.DKGRAY);return;}
            float size=Math.min(getWidth()-dp(34),getHeight()-dp(60)); float left=(getWidth()-size)/2,top=dp(12); RectF r=new RectF(left,top,left+size,top+size); float angle=-90;
            for(int i=0;i<values.size();i++){float sweep=values.get(i)*360f/total;p.setColor(colors[i%colors.length]);p.setStyle(Paint.Style.FILL);c.drawArc(r,angle,sweep,true,p);angle+=sweep;}
            float y=top+size+dp(24),x=dp(12); for(int i=0;i<labels.size();i++){p.setColor(colors[i%colors.length]);c.drawRect(x,y-dp(9),x+dp(9),y,p);String s=labels.get(i)+" "+String.format(java.util.Locale.US,"%.1f",values.get(i));drawText(c,s,x+dp(13),y,10,Color.DKGRAY); x+=Math.min(dp(130),dp(22)+p.measureText(s)); if(x>getWidth()-dp(100)){x=dp(12);y+=dp(19);} }
        }
    }
}
