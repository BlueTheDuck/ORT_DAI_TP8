package com.ducklings_corp.tp8;

import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

import java.util.ArrayList;

public class Game {
    CCGLSurfaceView _GameView;
    CCSize _screen;
    ArrayList<Sprite> _gaymers = new ArrayList<>();
    int _spriteTouched = -1;

    public Game(CCGLSurfaceView GameView) {
        Log.d("clsGame", "clsgame");
        _GameView = GameView;
    }

    public void startGame() {
        Log.d("clsGame", "startgame");

        Director.sharedDirector().attachInView(_GameView);

        _screen = Director.sharedDirector().displaySize();
        Log.d("clsGame", "Pantalla - Ancho: " + _screen.getWidth() + " - Alto: " + _screen.getHeight());

        Scene scene = startScene();
        Director.sharedDirector().runWithScene(scene);
    }

    private Scene startScene() {
        Log.d("clsGame", "start");
        Scene returnScene;
        returnScene = Scene.node();

        Log.d("clsGame", "startScene");
        GameLayer aLayer = new GameLayer();
        returnScene.addChild(aLayer);

        return returnScene;
    }

    class GameLayer extends Layer {
        public GameLayer() {
            setIsTouchEnabled(true);
            place();
            place();
            while (intersectionWithSprites(_gaymers.get(0), _gaymers.get(1))) {
                CCPoint initialPos = new CCPoint();
                initialPos.x = (float) Math.random() * _screen.getWidth();
                initialPos.y = (float) Math.random() * _screen.getHeight();
                _gaymers.get(1).setPosition(initialPos.x, initialPos.y);
                Log.d("GameLayer", "Relocationg");
            }
        }

        public void place() {
            Sprite gaymer = Sprite.sprite("gaymer.jpg");
            CCPoint initialPos = new CCPoint();
            initialPos.x = (float) Math.random() * _screen.getWidth();
            initialPos.y = (float) Math.random() * _screen.getHeight();
            gaymer.setPosition(initialPos.x, initialPos.y);
            _gaymers.add(gaymer);

            super.addChild(gaymer);
        }

        public boolean intersectionWithSprites(Sprite Sprite1, Sprite Sprite2) {
            Boolean intersection = false;
            Float sp1Top, sp1Right, sp1Bot, sp1Left,
                    sp2Top, sp2Right, sp2Bot, sp2Left;

            sp1Top = Sprite1.getPositionY() + Sprite1.getHeight() / 2;
            sp1Bot = Sprite1.getPositionY() - Sprite1.getHeight() / 2;
            sp1Right = Sprite1.getPositionX() + Sprite1.getWidth() / 2;
            sp1Left = Sprite1.getPositionX() - Sprite1.getWidth() / 2;
            sp2Top = Sprite2.getPositionY() + Sprite2.getHeight() / 2;
            sp2Bot = Sprite2.getPositionY() - Sprite2.getHeight() / 2;
            sp2Right = Sprite2.getPositionX() + Sprite2.getWidth() / 2;
            sp2Left = Sprite2.getPositionX() - Sprite2.getWidth() / 2;


            if (sp1Top >= sp2Bot && sp1Top <= sp2Top &&
                    sp1Right >= sp2Left && sp1Right <= sp2Right) {
                intersection = true;
            }
            if (sp1Top >= sp2Bot && sp1Top <= sp2Top &&
                    sp1Left >= sp2Left && sp1Left <= sp2Right) {
                intersection = true;
            }
            if (sp1Bot >= sp2Bot && sp1Bot <= sp2Top &&
                    sp1Right >= sp2Left && sp1Right <= sp2Right) {
                intersection = true;
            }
            if (sp1Bot >= sp2Bot && sp1Bot <= sp2Top &&
                    sp1Left >= sp2Left && sp1Left <= sp2Right) {
                intersection = true;
            }
            if (sp2Top >= sp1Bot && sp2Top <= sp1Top &&
                    sp2Right >= sp1Left && sp2Right <= sp1Right) {
                intersection = true;
            }

            if (sp2Top >= sp1Bot && sp2Top <= sp1Top &&
                    sp2Left >= sp1Left && sp2Left <= sp1Right) {
                intersection = true;
            }
            if (sp2Bot >= sp1Bot && sp2Bot <= sp1Top &&
                    sp2Right >= sp1Left && sp2Right <= sp1Right) {
                intersection = true;
            }
            if (sp2Bot >= sp1Bot && sp2Bot <= sp1Top &&
                    sp2Left >= sp1Left && sp2Left <= sp1Right) {
                intersection = true;
            }
            return intersection;
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            float x = event.getX(), y = _screen.getHeight() - event.getY();// Fuck Cocos

            _spriteTouched = intersectionWithPoint(x,y);

            return super.ccTouchesBegan(event);
        }

        public boolean ccTouchesMoved(MotionEvent event){
            float x = event.getX(), y = _screen.getHeight() - event.getY();
            move(x,y);
            return true;
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event) {
            float x = event.getX(), y = _screen.getHeight() - event.getY();
            _spriteTouched = -1;
            return super.ccTouchesEnded(event);
        }

        void move(float x, float y){
            if(_spriteTouched!=-1) {
                _gaymers.get(_spriteTouched).setPosition(x,y);
                if(intersectionWithSprites(
                        _gaymers.get(_spriteTouched),
                        _gaymers.get(_spriteTouched^1)
                )) {
                    Sprite _moveMe = _gaymers.get(_spriteTouched^1);
                    IntervalAction squareSeq = Sequence.actions(
                            MoveBy.action(1,   0, 100),
                            MoveBy.action(1, 100,   0),
                            MoveBy.action(1,   0,-100),
                            MoveBy.action(1,-100,   0)
                    );
                    _moveMe.runAction(squareSeq);
                }
            }
        }

        int intersectionWithPoint(float x,float y) {
            int spriteTouched = -1;
            for(int i = 0;i< _gaymers.size();i++) {
                Sprite gaymer = _gaymers.get(i);
                float spLeft = gaymer.getPositionX() - gaymer.getWidth() / 2,
                    spRight = spLeft + gaymer.getWidth();
                float spBot = gaymer.getPositionY() + gaymer.getHeight() / 2,
                        spTop = spBot - gaymer.getWidth();
                if(  spLeft < x  && x < spRight
                 && spTop < y && y < spBot) {
                    spriteTouched = i;
                    Log.d("intersectionWithPoint", "Collition with "+i);
                }
            }
            return spriteTouched;
        }
    }
}