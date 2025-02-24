import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chatbot',
  imports: [FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.scss',
})
export class ChatbotComponent {
  constructor(private authservice: AuthService) {}
  question: string = '';
  response: string = '';
  sendQuestion(){
    this.authservice.getAnswer(this.question).subscribe(
      (data)=>{
        this.response=data.response;
      },
      (error)=>{
        console.error('Erreur',error);
      }
      
    )
  }


}
